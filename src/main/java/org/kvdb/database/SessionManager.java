package org.kvdb.database;

import java.util.concurrent.atomic.AtomicInteger;

public class SessionManager {
    private AtomicInteger sessionId = new AtomicInteger();
    private Database inMemDb = new InMemoryDatabase();

    public Session createSession() {
        return new InMemorySessionImpl(inMemDb, sessionId.getAndIncrement());
    }
}