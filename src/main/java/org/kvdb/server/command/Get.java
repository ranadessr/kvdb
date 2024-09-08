package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Get implements Command {
    private String key;

    public Get(String key) {
        this.key = key;
    }

    public String run(Session sess) {
        return sess.get(key);
    }
}