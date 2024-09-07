# kvdb - A transactional key value store

kvdb implements a key/value store with transactional updates

## The following operations are supported
- GET `<key>`
- PUT `<key> <value>`
- DELETE `<key>`

The above operations can be run inside or outside of a transaction. If run outside of a transaction, the modifications are applied immediately. If run inside a transaction, updates are applied atomically such that all or none succeed.
