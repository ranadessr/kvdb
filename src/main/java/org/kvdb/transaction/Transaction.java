package org.kvdb.transaction;

public interface Transaction {
    void commit();
    void rollback();
}