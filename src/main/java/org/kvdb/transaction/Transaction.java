package org.kvdb.transaction;

import java.util.List;

public interface Transaction {
    int getId();
    List<Operation> getOperations();
    void addOperation(Operation op);
}