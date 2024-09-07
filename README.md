# kvdb - A transactional key value store

kvdb implements a simple key/value store with transactional updates

## The following operations are supported

- GET `<key>`
- PUT `<key> <value>`
- DELETE `<key>`

`key` and `value` are of type String

The above operations can be run inside or outside a transaction. If run outside a transaction, the modifications are applied immediately. If run inside a transaction, updates are applied atomically such that all or none succeed. kvdb uses [optimistic concurrency control](https://en.wikipedia.org/wiki/Optimistic_concurrency_control) to implement transactions. When committing a transaction, it compares old and new values for all keys being modified or deleted. If all modifications/deletes match, the transaction commits otherwise it rolls back.

## Building kvdb

```
git clone https://github.com/ranadessr/kvdb/
mvn clean package
java -classpath target/kvdb-1.0-SNAPSHOT.jar org.kvdb.App # runs example usage App
```

## Running kvdb

kvdb can be run as an embedded database or as a server process

### Running as embedded database

In this mode the database is like a transactional `Map`. The following is an example usage

```
import org.kvdb.database.Session;
import org.kvdb.database.SessionManager;

// ...
  SessionManager sessMgr = new SessionManager();
  Session sess = sessMgr.createSession();

  sess.startTransaction();
  sess.put("foo", "bar");
  sess.put("baz", "bat");
  sess.delete("baz");
  sess.commitTransaction();
  System.out.println(sess.get("baz"));
  System.out.println(sess.get("foo"));

```
A reference to the db is obtained via a `SessionManager` instance. Each `Session` is a connection to the db. For a full example refer to `App.java`


### Running as a server process

