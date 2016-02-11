# Introduction #

To get internal diagnostic information there is a Dump Diagnostic feature since version 0.8. Diagnostic information is written to the SD-Card in the file `KolabDiagDump.txt`.

**No personal information or contact names will be saved. The only thing someone could read is your google email address and your name. Edit the file before sending, if you don't like it**


# Details #

How to create a Diagnostic Dump:

1) Go to "Accounts & sync" -> "KolabDroid" -> "KolabDroid status"

![http://kolab-android.googlecode.com/svn/wiki/docimages/01.png](http://kolab-android.googlecode.com/svn/wiki/docimages/01.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/04.png](http://kolab-android.googlecode.com/svn/wiki/docimages/04.png)
![http://kolab-android.googlecode.com/svn/wiki/docimages/05.png](http://kolab-android.googlecode.com/svn/wiki/docimages/05.png)

2) Press the Menu key and select "Diag. Dump"

![http://kolab-android.googlecode.com/svn/wiki/docimages/23.png](http://kolab-android.googlecode.com/svn/wiki/docimages/23.png)

3) A notification should report success. If the SD Card is not available (e.g. because it's used by your PC) an error is reported.

![http://kolab-android.googlecode.com/svn/wiki/docimages/24.png](http://kolab-android.googlecode.com/svn/wiki/docimages/24.png)

4) Connect your phone to your PC, pick up the file and send it to a person/group you trust.

4a) Step 4 can also be done on your phone directly ;-)

# Example #

```
Kolab Droid Diagnostic Dump
===========================
Version: 0.8

SyncAdapterTypes: 
accountType = com.htc.htctwitter; authority = com.htc.chirp.provider.Tweet; supportsUploading = false; isUserVisible = true
accountType = com.twitter.android.auth.login; authority = com.twitter.android.provider.TwitterProvider; supportsUploading = false; isUserVisible = true
accountType = com.htc.cs; authority = com.htc.connectedservice.csprovider; supportsUploading = true; isUserVisible = true
accountType = com.htc.android.mail.eas; authority = htceas; supportsUploading = true; isUserVisible = true
accountType = com.htc.htctwitter; authority = com.htc.htctwitter.Users; supportsUploading = false; isUserVisible = true
accountType = com.htc.android.Stock; authority = stock; supportsUploading = true; isUserVisible = true
accountType = XING; authority = com.android.contacts; supportsUploading = false; isUserVisible = true
accountType = com.htc.newsreader; authority = com.htc.googlereader; supportsUploading = true; isUserVisible = true
accountType = com.htc.socialnetwork.flickr; authority = com.htc.socialnetwork.flickr.provider.StreamProvider; supportsUploading = false; isUserVisible = true
accountType = at.dasz.kolabdroid; authority = com.android.contacts; supportsUploading = true; isUserVisible = true
accountType = com.google; authority = com.android.contacts; supportsUploading = true; isUserVisible = true
accountType = com.htc.sync.provider.weather; authority = com.htc.sync.provider.weather; supportsUploading = true; isUserVisible = true
accountType = com.google; authority = gmail-ls; supportsUploading = true; isUserVisible = true
accountType = com.google; authority = subscribedfeeds; supportsUploading = true; isUserVisible = false
accountType = at.dasz.kolabdroid; authority = com.android.calendar; supportsUploading = true; isUserVisible = true
accountType = com.twitter.android.auth.login; authority = com.android.contacts; supportsUploading = false; isUserVisible = true
accountType = com.htc.socialnetwork.flickr; authority = com.android.contacts; supportsUploading = false; isUserVisible = true
accountType = com.htc.cs; authority = com.htc.wdm.provider.WDMProvider; supportsUploading = true; isUserVisible = true
accountType = com.google; authority = com.android.calendar; supportsUploading = true; isUserVisible = true


Accounts: 
Name = Weather; Type = com.htc.sync.provider.weather
Name = Stocks; Type = com.htc.android.Stock
Name = arthur.zaczek@gmail.com; Type = com.google
Name = News; Type = com.htc.newsreader
Name = arthur.zaczek; Type = com.htc.socialnetwork.flickr
Name = KolabDroid SyncAccount; Type = at.dasz.kolabdroid


First 10 contacts: 
ID = 313; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 313; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 314; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 314; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 315; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 315; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 316; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 316; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 317; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 317; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 318; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 318; HAS_PHONE_NUMBER = 0; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 319; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 319; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 320; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 320; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 321; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 321; HAS_PHONE_NUMBER = 0; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 322; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 322; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0

ID = 323; ACCOUNT_NAME = KolabDroid SyncAccount; ACCOUNT_TYPE = at.dasz.kolabdroid; deleted = 0
ID = 323; HAS_PHONE_NUMBER = 1; IN_VISIBLE_GROUP = 1; STARRED = 0



Calendars: 
Our Calendar ID = 5
ID = 1, name = null; displayName = PC Sync; _sync_account = PC Sync; _sync_account_type = com.htc.pcsc, selected = 0, sync_events = 1
ID = 2, name = Arthur Zaczek; displayName = Arthur Zaczek; _sync_account = arthur.zaczek@gmail.com; _sync_account_type = com.google, selected = 0, sync_events = 1
ID = 4, name = null; displayName = Flickr; _sync_account = arthur.zaczek; _sync_account_type = com.htc.socialnetwork.flickr, selected = 1, sync_events = 1
ID = 5, name = KolabDroid SyncAccount; displayName = KolabDroid SyncAccount; _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid, selected = 1, sync_events = 1


First 10 calendar entries: 
Our Calendar ID = 5
ID = 1128, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1129, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1130, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1131, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1132, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1133, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1134, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1135, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1136, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1137, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
ID = 1138, _sync_account = KolabDroid SyncAccount; _sync_account_type = at.dasz.kolabdroid
```