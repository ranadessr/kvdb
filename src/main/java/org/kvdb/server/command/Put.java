package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Put implements Command {
    private String key;
    private String value;

    public Put(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String run(Session sess) {
        sess.put(key, value);
        return "";
    }
}