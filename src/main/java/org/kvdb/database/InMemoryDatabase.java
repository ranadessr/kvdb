package org.kvdb.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryDatabase implements Database {
    private Map<String, String> store = new ConcurrentHashMap<>();
    private Lock lock = new ReentrantLock();

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
    public Lock dbLock() {
        return this.lock;
    }
}