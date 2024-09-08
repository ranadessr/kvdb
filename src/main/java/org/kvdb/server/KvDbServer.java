package org.kvdb.server;

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
            ctx.channel().writeAndFlush("ECHO " + msg + "\n");
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
        private void handleCommand(String cmd) {
            try {

            } catch (Exception e) {
            }
        }
    }
}