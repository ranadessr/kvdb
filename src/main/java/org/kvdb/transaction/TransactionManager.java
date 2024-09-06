package org.kvdb.transaction;

public interface TransactionManager {
    Transaction startTransaction();
    void commit(Transaction t);
    void rollback(Transaction t);
}