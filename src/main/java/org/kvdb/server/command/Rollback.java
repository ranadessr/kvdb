package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Rollback implements Command {
    public String run(Session sess) {
        sess.rollbackTransaction();
        return "";
    }
}