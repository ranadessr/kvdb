package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Delete implements Command {
    private String key;
    public Delete(String key) {
        this.key = key;
    }
    public String run(Session sess) {
        sess.delete(key);
        return "";
    }
}