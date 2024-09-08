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
mvn clean compile assembly:single
```
The above command will produce a jar with all needed dependencies in the `target` dir

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

- start the server process as follows
  ```java -Dport=8085 -classpath target/kvdb-1.0-SNAPSHOT-jar-with-dependencies.jar org.kvdb.App # runs example usage App```
- the port is optional. If not specified, the server starts on port 8080

Once started the server is ready to receive commands based on the following protocol

```
Data Modification Commands
Data Modification Commands can be issued individually or as a part of a transaction.


# add or update a key/value pair – overwrites existing value

PUT [key] [value]


# retrieve a value by key – retrieves latest value from all committed transactions

GET [key]


# delete a value by key

DEL [key]


Transaction Control Commands
# start a transaction

START


# commit a transaction

COMMIT


# rollback a transaction – discard changes

ROLLBACK
```
