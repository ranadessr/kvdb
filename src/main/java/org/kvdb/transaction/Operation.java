package org.kvdb.transaction;

public abstract class Operation {
    protected String name;
    public String getOperation() {
        return this.name;
    }
}