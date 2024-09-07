package org.kvdb.transaction;

public class Delete extends Operation {
    private String key;
    private String oldValue;
    public Delete(String key, String oldValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.name = "DELETE";
    }
}
