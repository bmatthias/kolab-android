The actual synchronization algorithm is almost independent of the actual source and destination, and totally independent of the synchronized data items.

# Important Classes #

  * `CacheEntry`: holds all data to connect remote and local items and recognize changes in them
  * `SyncWorker`: the background worker thread that holds the main synchronization algorithm (`SyncWorker.sync()`)
  * `SyncHandler` interface: defines the necessary operations to be able to synchronize a local data store
  * `AbstractSyncHandler`: common code for all local data stores

The main data structure holding this together is the `SyncContext` and the `CacheEntry`.

The **cache entry** holds the local hash and ID, as well as the remote Id (kolab uid), the IMAP UID and the remote changed date and size. This information allows us to find entries and recognize when they change. The local hash is a store-specific hash over properties, since the android doesn't notify us when an item has changed.

The **context** connects the cache entry with the "real" objects. On the local side the business object and on the remote side the IMAP message.

# Auxiliary Classes #

  * `BaseWorker`: basic threading and status management
  * `StatusEntry`: log file data
  * `SyncContext`: "cursor" item for the currently synchronized item
  * `SyncException`: application specific exception
  * `SyncService`, `SyncServiceManager`: keep the synchronization running in the background

# Algorithm #

Based on the synchronization algorithm of [synckolab](http://www.gargan.org/en/Mozilla_Extensions/SyncKolab/FAQ/), the `SyncWorker` follows this plan (numbers reference comments and log messages in the source code):

  * (1) retrieve list of all imap message headers
  * for all messages:
    * (2) check message headers for changes
    * (5) fetch local cache entry
    * local entry found?
      * (6) no => save locally
      * (7) yes => compare data to figure out what happened
        * cur = local entry:
          * a: check for local changes and upload them
          * d: entry missing => delete on server
        * cur != local entry:
          * b: no local changes found: updating local item from server
          * c: local changes found: conflicting, updating local item from server
      * (8) remember message as processed
  * (9) for all unprocessed local items
    * a: skip already processed from server
    * b: found in local cache: deleting locally
    * c: not found in local cache: creating on server

# Kolab data format #

The messages on the server comply with the [kolab specs](http://www.kolab.org/doc/kolabformat-2.0rc7-html/index.html)