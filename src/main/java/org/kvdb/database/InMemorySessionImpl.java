package org.kvdb.database;

import org.kvdb.transaction.Transaction;

public class InMemorySessionImpl implements Session {
    private Database db;
    private int sessionId;
    private boolean inTransaction = false;
    public InMemorySessionImpl(Database db, int sessionId) {
        this.db = db;
        this.sessionId = sessionId;
    }
    public void put(String key, String value) {
        if (inTransaction) {

        }
        this.db.put(key, value);
    }
    public String get(String key) {
        if (inTransaction) {

        }
        String value = this.db.get(key);
        return value;
    }
    public void delete(String key) {
        if (inTransaction) {

        }
        this.db.delete(key);
    }

    @Override
    public Transaction startTransaction() {
        inTransaction = true;
        return null;
    }

    @Override
    public void commitTransaction() {
        inTransaction = false;
    }

    @Override
    public void rollbackTransaction() {
        inTransaction = false;
    }

    @Override
    public void close() {

    }
}