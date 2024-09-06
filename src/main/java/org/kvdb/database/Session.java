package org.kvdb.database;

import org.kvdb.transaction.Transaction;

public interface Session {
    Transaction startTransaction();
    void commitTransaction();
    void rollbackTransaction();
    void close();
    void put(String key, String value);
    String get(String key);
    void delete(String key);
}