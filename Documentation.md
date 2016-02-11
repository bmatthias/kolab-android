# Introduction #

This is a short document of how to use KolabDroid.

Before you start:

  * Don't use it if you rely on it
  * Have a backup or use test data
  * Know how to recover your contacts and calendar events
  * Read the [VersionHistory](VersionHistory.md)

# Troubleshooting #

Yeah, the first topic. This is a development version, troubles are expected!

  * Send issues
  * Share your experience and problems on the [mailing list](http://groups.google.com/group/kolab-connector-discuss)
  * If you know adb: use it
  * Use [ErrorReporting](ErrorReporting.md)
  * Use [DiagnosticDump](DiagnosticDump.md)
  * If you know Java and Eclipse: use it and send patches

# Details #

Kolab Droid is a synchronisation provider. That means, that there is no icon to launch the app. Instead you can configure it under "Settings" -> "Accounts & sync".

As a synchronisation provider, KolabDroid keeps your contacts and calendar events isolated from your other accounts (Google, Exchange, etc.). No data will be leaked through your other accounts.

# Installation #

Since this is only a developer preview, the app is not distributed via the market and you have to enable "Settings" -> "Applications" -> "Unknown sources". To reduce the security risk from this setting, you can disable it after installing the app.

You have 3 options:

  * Use the QR Code on the [download page](http://code.google.com/p/kolab-android/downloads/list)
  * Download the APK, save it on your SD Card and install it with an File Manager
  * Download the APK and use `adb install KolabDroid-...apk`

Since version 0.6 upgrades should make no troubles.

# How to use it #

After installation goto "Settings" -> "Accounts & sync"

![http://kolab-android.googlecode.com/svn/wiki/docimages/01.png](http://kolab-android.googlecode.com/svn/wiki/docimages/01.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/02.png](http://kolab-android.googlecode.com/svn/wiki/docimages/02.png)

Press the menu key and add a new account. If the account already exists, nothing will happen. We support only one account for now.

![http://kolab-android.googlecode.com/svn/wiki/docimages/03.png](http://kolab-android.googlecode.com/svn/wiki/docimages/03.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/04.png](http://kolab-android.googlecode.com/svn/wiki/docimages/04.png)

Open this account and touch "KolabDroid settings"

![http://kolab-android.googlecode.com/svn/wiki/docimages/05.png](http://kolab-android.googlecode.com/svn/wiki/docimages/05.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/06.png](http://kolab-android.googlecode.com/svn/wiki/docimages/06.png)

Edit your settings: Enter IMAP host, username and password. Then touch "Test IMAP settings and update folder lists"

![http://kolab-android.googlecode.com/svn/wiki/docimages/07.png](http://kolab-android.googlecode.com/svn/wiki/docimages/07.png)

If the test was successful you can select your contacts and calendar IMAP folders. At the end you can choose the minimum interval between synchronisations to save traffic and battery.

Press the back key to save your changes.

Select the data sources you want to sync.

![http://kolab-android.googlecode.com/svn/wiki/docimages/08.png](http://kolab-android.googlecode.com/svn/wiki/docimages/08.png)

Synchronisation should start automatically. If you touch "KolabDroid status" you can see the progress and logfile.

![http://kolab-android.googlecode.com/svn/wiki/docimages/09.png](http://kolab-android.googlecode.com/svn/wiki/docimages/09.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/10.png](http://kolab-android.googlecode.com/svn/wiki/docimages/10.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/11.png](http://kolab-android.googlecode.com/svn/wiki/docimages/11.png)

Synchronisation will be started, when Android thinks it is required. You can only configure the minimum interval.

We didn't find a documentation about this topic, but we observed that a sync is started at least once a day or when something has changed on your phone.

Anyways, you can start a synchronisation at any time by pressing the menu key and "Sync now"

![http://kolab-android.googlecode.com/svn/wiki/docimages/12.png](http://kolab-android.googlecode.com/svn/wiki/docimages/12.png)

# Addressbook #

After the fist sync, when you open your phone book, it will be empty. This is because the contact manager will only show accounts that are configured to be displayed. You can change that easily.

![http://kolab-android.googlecode.com/svn/wiki/docimages/13.png](http://kolab-android.googlecode.com/svn/wiki/docimages/13.png)

Press the menu key -> "Display options"

![http://kolab-android.googlecode.com/svn/wiki/docimages/14.png](http://kolab-android.googlecode.com/svn/wiki/docimages/14.png)

A list of all displayable accounts is shown. Select the "KolabDroid SyncAccount"

![http://kolab-android.googlecode.com/svn/wiki/docimages/15.png](http://kolab-android.googlecode.com/svn/wiki/docimages/15.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/16.png](http://kolab-android.googlecode.com/svn/wiki/docimages/16.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/17.png](http://kolab-android.googlecode.com/svn/wiki/docimages/17.png)

Touch "Done" -> your contacts should be shown.

![http://kolab-android.googlecode.com/svn/wiki/docimages/18.png](http://kolab-android.googlecode.com/svn/wiki/docimages/18.png)

**We know that some people have troubles at this point. Please send the [DiagnosticDump](DiagnosticDump.md) file and screenshots to the [issue tracker](http://code.google.com/p/kolab-android/issues/list) to locate the problem.**

# Calendar #

Same procedure as with your address book:

![http://kolab-android.googlecode.com/svn/wiki/docimages/19.png](http://kolab-android.googlecode.com/svn/wiki/docimages/19.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/20.png](http://kolab-android.googlecode.com/svn/wiki/docimages/20.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/21.png](http://kolab-android.googlecode.com/svn/wiki/docimages/21.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/22.png](http://kolab-android.googlecode.com/svn/wiki/docimages/22.png)

# Editing Contacts #

On many phones, the default Contact Manager is not able to edit non-standard contacts. This seems to be inherited from the original Android code, it does not correctly interpret non-native contacts. While there are provisions in Google's code for the Contact Manager to allow synchronization providers to supply their own editor template (which fields can be edited with which controls) there is no way to attach to this extension point. Fixing this requires changing the android source (volunteers welcome!). Interestingly enough, there are some phones/versions that are able to correctly edit Kolab Contacts, for example the A1 ROM for the RBM2.

For now, we have implemented an alternative editor which can be chosen instead of the Contact Manager. If you only have Kolab Contacts, you can set it as default.

**Note: Some Android versions do support editing our contacts. Please test and report on the [mailing list](http://groups.google.com/group/kolab-connector-discuss).**


![http://kolab-android.googlecode.com/svn/wiki/docimages/25.png](http://kolab-android.googlecode.com/svn/wiki/docimages/25.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/26.png](http://kolab-android.googlecode.com/svn/wiki/docimages/26.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/27.png](http://kolab-android.googlecode.com/svn/wiki/docimages/27.png)

Choose "Edit Kolab Contact".

![http://kolab-android.googlecode.com/svn/wiki/docimages/28.png](http://kolab-android.googlecode.com/svn/wiki/docimages/28.png)