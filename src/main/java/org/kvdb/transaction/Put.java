package org.kvdb.transaction;

public class Put extends Operation {
    private String key;
    private String oldValue;
    private String newValue;

    public Put(String key, String oldValue, String newValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.name = "PUT";
    }
}
