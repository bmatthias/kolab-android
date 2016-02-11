# Upcoming #
  * Bugfixes

# 0.9 #
  * Editor: Support all phone contact fields
  * Editor: Support 3 email address fields
  * Editor: Support photos
  * Editor: Implemented adding phone numbers to contacts out of call list
  * Common: Better contact sync & management
  * Common: Ignore already processed message id's from server. these are duplicated mails
  * Common: Bugfixes
  * Calendar: Fix Daylight Bug in Calendar, recurrent events and all day events should not shift anymore

# 0.8 #
  * bugfixes, analyzed crash reports
  * Diagnostic Dump
  * Syncing Contacts rewritten, preserves unknown Entries now
  * Improved Contact Editor
  * [Issue 51](https://code.google.com/p/kolab-android/issues/detail?id=51): Contact names not imported correctly - fixed, do not touch fullname
  * [Issue 49](https://code.google.com/p/kolab-android/issues/detail?id=49): URL (Website) item not being synced - implemented
  * Know problem: [Issue 34](https://code.google.com/p/kolab-android/issues/detail?id=34): No sync/edit of Postal Address
  * Know problem: [Issue 57](https://code.google.com/p/kolab-android/issues/detail?id=57): Contact Editor supports only a couple of phone numbers
  * Know problem: [Issue 58](https://code.google.com/p/kolab-android/issues/detail?id=58): Contact Editor supports only 3 EMail addresses
  * Know problem: [Issue 23](https://code.google.com/p/kolab-android/issues/detail?id=23): Calendar: recurring all day events are not handling end date correctly
  * Be aware, Version 0.7 and/or 0.6 screwed up some phone numbers (in one case 9 of 112). They will/are lost during the next sync.

# 0.7 #

  * bugfixes, analyzed crash reports
  * only one APK for 2.1 and >= 2.2
  * catch Messaging Exceptions on individual items to prevent sync from stopping
  * Ability to view and report fatal sync error messages
  * Fixed wrong mapping of min sync interval Spinner
  * Made Account Manager looks nicer

# 0.6 #
  * Major changes, see release notes
  * switch to froyo branch, supporting Accounts & Synchronization
  * support for own contacts and calendar items, no more conflicts with google accounts

# 0.5 #
  * added acra error reporter
  * fixed [Issue 26](https://code.google.com/p/kolab-android/issues/detail?id=26): NullPointerException on Sync All (kolab-android 0.4, Froyo)
  * retrieve IMAP Folders in settings view
  * new preference value to save IMAP namespace
  * improved settings test

# 0.4 #

  * Set min SDK to Android 1.6
  * Fix sync loop if no birthday given
  * Set ID of contact cache entry correctly
  * Calendar Provider should not crash anymore.
  * Performance of processing items has been improved.
  * many other bugfixes


# 0.3 #

  * Solved [Issue #18](https://code.google.com/p/kolab-android/issues/detail?id=#18): Syncing by pressing the "Sync" Button works with an SSL Certificate.
  * Added synchronisation for contact notes and contact photos. Photos are yet only synced  from Kolab to Android, not vice versa.
  * [issue 13](https://code.google.com/p/kolab-android/issues/detail?id=13): added trust manager factory, supports non trusted SSL Certificates
  * many other bugfixes