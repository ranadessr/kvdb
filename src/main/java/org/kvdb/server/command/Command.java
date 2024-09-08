package org.kvdb.server.command;

import org.kvdb.database.Database;
import org.kvdb.database.Session;

public interface Command {
    public String run(Session sess);
}