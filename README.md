# kvdb - A transactional key value store

kvdb implements a simple key/value store with transactional updates

## The following operations are supported
- GET `<key>`
- PUT `<key> <value>`
- DELETE `<key>`

The above operations can be run inside or outside a transaction. If run outside a transaction, the modifications are applied immediately. If run inside a transaction, updates are applied atomically such that all or none succeed. kvdb uses [optimistic concurrency control](https://en.wikipedia.org/wiki/Optimistic_concurrency_control) to implement transactions. When committing a transaction, it compares old and new values for all keys being modified or deleted. If all modifications/deletes match, the transaction commits otherwise it rolls back.

## Running kvdb
kvdb can be run as an embedded database or as a server process

### Running as embedded database

### Running as a server process

