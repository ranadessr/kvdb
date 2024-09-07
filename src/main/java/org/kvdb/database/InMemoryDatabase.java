package org.kvdb.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase implements Database {
    private Map<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    @Override
    public void delete(String key) {
        store.remove(key);
    }
}