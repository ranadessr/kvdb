package org.kvdb.database;

public interface Database {
    void put(String key, String value);
    String get(String key);
    void delete(String key);
}