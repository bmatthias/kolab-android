# Location of Code #
The Repository is located on gitorious:

http://gitorious.org/kolab-android/kolab-android

The Source Code of each released version is cloned to googles code project SVN Repository.

# Local Libraries #

The bulk of the source code are local copies of the javamail library and the JAF (Java Activation Framework) which are necessary to access the IMAP store. These parts are stored in the `javamail/` and `activation/` subdirectories.

The two libraries contain slight modifications to work without AWT and other non-android-supported APIs.

# KolabDroid #

The real functionality is located in the `src/` subdirectory. There are the following packages:

  * KolabDroid: main application entry points, interfaces and utilities
    * Sync: common synchronization algorithm and infrastructure
    * Calendar: calendar-specific code
    * Contacts: contacts-specific code
    * Imap
      * DchFactory: JAF glue to correctly parse messages
      * ImapClient: default IMAP setup
      * MessageHelper: utilities
    * Provider: sqlite wrapper for our local data
    * Settings: definition and UI for the settings dialog