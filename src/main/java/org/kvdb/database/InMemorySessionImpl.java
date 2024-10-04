package org.kvdb.database;

import org.kvdb.transaction.InMemoryTransaction;
import org.kvdb.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySessionImpl implements Session {
    private Database db;
    private int sessionId;
    private boolean inTransaction = false;
    private AtomicInteger transactionId = new AtomicInteger();
    private Transaction currentTransaction;

    // structures for maintaining uncommitted puts/deletes
    private Map<String, ModifiedValue> modifiedKeys = new HashMap<>();
    private Map<String, ModifiedValue> deletedKeys = new HashMap<>();

    // potentially useful for logging/debugging
    private List<Transaction> committedTransactions = new ArrayList<>();
    private List<Transaction> rolledBackTransactions = new ArrayList<>();

    public InMemorySessionImpl(Database db, int sessionId) {
        this.db = db;
        this.sessionId = sessionId;
    }

    public void put(String key, String value) {
        if (inTransaction) {
            String oldValue = this.db.get(key);
            modifiedKeys.put(key, new ModifiedValue(oldValue, value));
            deletedKeys.remove(key); // in case the key is deleted then modified, no need to delete
        } else
            this.db.put(key, value);
    }

    public String get(String key) {
        String value = this.db.get(key);
        if (inTransaction) {
            if (modifiedKeys.containsKey(key))
                value = modifiedKeys.get(key).newValue;
            if (deletedKeys.containsKey(key))
                value = null;
        }
        return value;
    }

    public void delete(String key) {
        if (inTransaction) {
            String oldValue = this.db.get(key);
            deletedKeys.put(key, new ModifiedValue(oldValue, oldValue));
            modifiedKeys.remove(key); // in case the key is modified then deleted, no need to modify
        } else
            this.db.delete(key);
    }

    @Override
    public Transaction startTransaction() {
        if (inTransaction)
            throw new RuntimeException("Transaction with id " + currentTransaction.getId() + " in progress");
        inTransaction = true;
        currentTransaction = new InMemoryTransaction(transactionId.getAndIncrement());
        return currentTransaction;
    }

    @Override
    public void commitTransaction() {
        if (!inTransaction)
            throw new RuntimeException("No active transaction to commit");
        // go thru modifiedKeys and deletedKeys
        // for all deleted/modified keys, check that old value matches value in db
        // if any of the values to be committed do not match, roll back the transaction
        // otherwise update all values

        // acquire lock to prevent other threads from committing
        this.db.dbLock().lock();
        try {
            Set<ModifiedValue> failedModifiedKeys = compareOldNewValues(modifiedKeys);
            Set<ModifiedValue> failedDeletedKeys = compareOldNewValues(deletedKeys);
            if (!failedModifiedKeys.isEmpty() || !failedDeletedKeys.isEmpty()) {
                rollbackTransaction();
                throw new RuntimeException(getFailedKeys(failedModifiedKeys));
            } else {
                System.out.println("Committing transaction " + currentTransaction.getId());
                applyPuts(modifiedKeys);
                applyDeletes(deletedKeys.keySet());
                cleanupTransaction(true);
            }
        }
        finally {
            this.db.dbLock().unlock();
        }
    }

    @Override
    public void rollbackTransaction() {
        if (!inTransaction)
            throw new RuntimeException("No active transaction to rollback");
        System.out.println("Rolling back transaction " + currentTransaction.getId());
        cleanupTransaction(false);
    }

    @Override
    public void close() {

    }

    private String getFailedKeys(Set<ModifiedValue> modifiedKeys) {
        StringBuffer failedKeys = new StringBuffer();

        for (ModifiedValue val : modifiedKeys) {
            if (val.oldValue.equalsIgnoreCase(val.newValue)) {
                failedKeys.append("DELETE failed ").append(val.oldValue).append("\n");
            }else {
                failedKeys.append("MODIFY failed ").append(val.oldValue).append(" differs ").append(val.newValue).append("\n");
            }
        }

        return failedKeys.toString();
    }

    private void cleanupTransaction(boolean isCommitted) {
        modifiedKeys.clear();
        deletedKeys.clear();
        if (isCommitted)
            committedTransactions.add(currentTransaction);
        else
            rolledBackTransactions.add(currentTransaction);
        inTransaction = false;
        currentTransaction = null;
    }

    private Set<ModifiedValue> compareOldNewValues(Map<String, ModifiedValue> updates) {
        Set<ModifiedValue> failedKeys = new HashSet<>();
        for (Map.Entry<String, ModifiedValue> update : updates.entrySet() ) {
            String key = update.getKey();
            ModifiedValue mod = update.getValue();
            if ((this.db.get(key) == null && mod.oldValue != null) || (this.db.get(key) !=null &&
                    !this.db.get(key).equals(mod.oldValue))) {
                failedKeys.add(update.getValue());
                break;
            }
        }
        return failedKeys;
    }

    private void applyPuts(Map<String, ModifiedValue> updates) {
        for (Map.Entry<String, ModifiedValue> update : updates.entrySet()) {
            this.db.put(update.getKey(), update.getValue().newValue);
        }
    }

    private void applyDeletes(Set<String> deletedKeys) {
        for (String key : deletedKeys) {
            this.db.delete(key);
        }
    }

    // POJO to keep track of old/new values for key
    // old value is committed value in db, new value is uncommitted value from PUT or DELETE
    // this will be used as a CAS check to decide if transaction can commit or not
    private static class ModifiedValue {
        String oldValue;
        String newValue;

        public ModifiedValue(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}