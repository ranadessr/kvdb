package org.kvdb.database;

import java.util.concurrent.locks.Lock;

public interface Database {
    void put(String key, String value);
    String get(String key);
    void delete(String key);
    Lock dbLock();
}