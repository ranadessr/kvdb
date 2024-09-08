package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Commit implements Command {
    public String run(Session sess) {
        sess.commitTransaction();
        return "";
    }
}