package org.kvdb.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.kvdb.database.Session;
import org.kvdb.database.SessionManager;
import org.kvdb.server.command.Command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KvDbServer {
    private final int port;
    private SessionManager sessionManager;

    public KvDbServer(SessionManager sessionManager, int port) {
        this.sessionManager = sessionManager;
        this.port = port;
    }
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {

                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new DbServerHandler(sessionManager));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static class DbServerHandler extends SimpleChannelInboundHandler<String> {
        private SessionManager sessionManager;
        private Map<Channel, Session> sessions = new ConcurrentHashMap<>();
        private CommandParser parser = CommandParser.getInstance();
        private ObjectMapper mapper = new ObjectMapper();

        public DbServerHandler(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            System.out.println("New client connected " + ctx.name());
            sessions.put(ctx.channel(), sessionManager.createSession());
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.print("received message on channel " + ctx.name() + ": " + msg);
            Session sess = sessions.get(ctx.channel());
            String response = handleCommand(sess, msg);
            ctx.channel().writeAndFlush(response + "\n");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
            System.out.println("Client disconnected");
            sessions.remove(ctx.channel());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
        private String handleCommand(Session sess, String cmd) {
            StringBuffer response = new StringBuffer();
            CommandStatus status = new CommandStatus();

            try {
                Command command = parser.parse(cmd);
                String resp = command.run(sess);
                status.status = "Ok";
                status.result = resp;
            } catch (Exception e) {
                status.status = "Error";
                status.mesg = e.getMessage();
            }
            try {
                return mapper.writeValueAsString(status);
            } catch (JsonProcessingException jpe) {
                return jpe.getMessage();
            }
        }
    }
    private static class CommandStatus {
        public String status;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String result;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String mesg;
    }
}