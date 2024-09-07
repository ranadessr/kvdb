package org.kvdb.transaction;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTransaction implements Transaction {
    private List<Operation> operations = new ArrayList<>();
    private final int id;
    public InMemoryTransaction(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public List<Operation> getOperations() {
        return operations;
    }
    public void addOperation(Operation op) {
        this.operations.add(op);
    }
}