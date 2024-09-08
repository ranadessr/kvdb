package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public class Start implements Command {
    public String run(Session sess) {
        sess.startTransaction();
        return "";
    }
}