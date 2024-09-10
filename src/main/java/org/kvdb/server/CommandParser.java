package org.kvdb.server;

import org.kvdb.server.command.Command;
import org.kvdb.server.command.Commit;
import org.kvdb.server.command.Delete;
import org.kvdb.server.command.Get;
import org.kvdb.server.command.Put;
import org.kvdb.server.command.Rollback;
import org.kvdb.server.command.Start;

import java.util.HashSet;
import java.util.Set;

public class CommandParser {
    /*
        valid commands: GET, PUT, DELETE, START, COMMIT, ROLLBACK
     */
    private final Set commands = new HashSet<>();
    private static final CommandParser instance = new CommandParser();
    private CommandParser() {
        commands.add("GET");
        commands.add("PUT");
        commands.add("DELETE");
        commands.add("START");
        commands.add("COMMIT");
        commands.add("ROLLBACK");
    }
    public static CommandParser getInstance() {
        return instance;
    }
    public Command parse(String msg) throws IllegalAccessException {
        String[] pieces = msg.trim().split(" ");
        if (pieces.length < 1)
            throw new IllegalArgumentException("Invalid command format " + msg);
        if (!commands.contains(pieces[0].toUpperCase()))
            throw new IllegalArgumentException("Unknown command : " + pieces[0]);
        if (pieces[0].equalsIgnoreCase("PUT")) {
            if (pieces.length < 3) throw new IllegalArgumentException("Bad PUT request " + msg);
            int firstIndex = msg.trim().indexOf(" ");
            int secondIndex = msg.trim().indexOf(" ", firstIndex+1);
            return new Put(pieces[1], msg.trim().substring(secondIndex+1));
        }
        if (pieces[0].equalsIgnoreCase("GET")) {
            if (pieces.length != 2) throw new IllegalArgumentException("Bad GET request " + msg);
            return new Get(pieces[1]);
        }
        if (pieces[0].equalsIgnoreCase("DELETE")) {
            if (pieces.length != 2) throw new IllegalArgumentException("Bad DELETE request " + msg);
            return new Delete(pieces[1]);
        }
        if (pieces[0].equalsIgnoreCase("START")) return new Start();
        if (pieces[0].equalsIgnoreCase("COMMIT")) return new Commit();
        if (pieces[0].equalsIgnoreCase("ROLLBACK")) return new Rollback();
        throw new IllegalArgumentException("Received unparseable command " + msg);
    }
}