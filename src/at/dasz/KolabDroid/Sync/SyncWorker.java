/*
 * Copyright 2010 Arthur Zaczek <arthur@dasz.at>, dasz.at OG; All rights reserved.
 * Copyright 2010 David Schmitt <david@dasz.at>, dasz.at OG; All rights reserved.
 *
 *  This file is part of Kolab Sync for Android.

 *  Kolab Sync for Android is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.

 *  Kolab Sync for Android is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with Kolab Sync for Android.
 *  If not, see <http://www.gnu.org/licenses/>.
 */

package at.dasz.KolabDroid.Sync;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.xml.parsers.ParserConfigurationException;

import org.acra.ErrorReporter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import at.dasz.KolabDroid.R;
import at.dasz.KolabDroid.StatusHandler;
import at.dasz.KolabDroid.Calendar.SyncCalendarHandler;
import at.dasz.KolabDroid.Imap.ImapClient;
import at.dasz.KolabDroid.Imap.TrustManagerFactory;
import at.dasz.KolabDroid.Provider.LocalCacheProvider;
import at.dasz.KolabDroid.Provider.StatusProvider;
import at.dasz.KolabDroid.Settings.Settings;

/**
 * The background worker that implements the main synchronization algorithm.
 */
public class SyncWorker extends BaseWorker
{
	// Not final to avoid warnings
	private static boolean	DBG_LOCAL_CHANGED	= false;
	private static boolean	DBG_REMOTE_CHANGED	= false;

	public SyncWorker(Context context)
	{
		super(context);
	}

	private static StatusEntry	status;

	public static StatusEntry getStatus()
	{
		return status;
	}

	@Override
	protected void runWorker()
	{
		setRunningMessage(R.string.syncisrunning);
		StatusProvider statProvider = new StatusProvider(context);
		try
		{
			StatusHandler.writeStatus(R.string.startsync);

			Settings settings = new Settings(this.context);
			SyncHandler handler = null;

			if (Build.VERSION.SDK_INT <= 6)
			{
				handler = new at.dasz.KolabDroid.Contacts.SyncContactsHandler(
						this.context);
			}
			else
			{
				handler = new at.dasz.KolabDroid.ContactsContract.SyncContactsHandler(
						this.context);
			}
			if (shouldProcess(handler))
			{
				status = handler.getStatus();
				try
				{
					sync(settings, handler);
				}
				catch (Exception ex)
				{
					// Save fatal sync exception
					status.setFatalErrorMsg(ex.toString());
					throw ex;
				}
				finally
				{
					statProvider.saveStatusEntry(status);
				}
			}

			if (isStopping())
			{
				StatusHandler.writeStatus(R.string.syncaborted);
				return;
			}

			handler = new SyncCalendarHandler(this.context);
			if (shouldProcess(handler))
			{
				status = handler.getStatus();
				try
				{
					sync(settings, handler);
				}
				catch (Exception ex)
				{
					// Save fatal sync exception
					status.setFatalErrorMsg(ex.toString());
					// Report
					ErrorReporter.getInstance().handleException(ex);
					throw ex;
				}
				finally
				{
					statProvider.saveStatusEntry(status);
				}
			}

			if (isStopping())
			{
				StatusHandler.writeStatus(R.string.syncaborted);
				return;
			}

			StatusHandler.writeStatus(R.string.syncfinished);
		}
		catch (Exception ex)
		{
			final String errorFormat = this.context.getResources().getString(
					R.string.sync_error_format);

			StatusHandler
					.writeStatus(String.format(errorFormat, ex.toString()));

			ex.printStackTrace();
		}
		finally
		{
			status = null;
			statProvider.close();
			StatusHandler.notifySyncFinished();
		}
	}

	private boolean shouldProcess(SyncHandler handler)
	{
		return handler.shouldProcess();
		/*
		 * return handler.getDefaultFolderName() != null &&
		 * !"".equals(handler.getDefaultFolderName());
		 */
	}

	private void sync(Settings settings, SyncHandler handler)
			throws MessagingException, IOException,
			ParserConfigurationException, SyncException, CertificateException
	{
		// handler.setSettings(settings); //handler should be able to react on
		// settings

		Store server = null;
		Folder sourceFolder = null;
		try
		{
			StatusHandler.writeStatus(R.string.fetching_local_items);
			handler.fetchAllLocalItems();
			if (isStopping()) return;
			
			StatusHandler.writeStatus(R.string.connect_server);

			if (settings.getUseSSL())
			{
				Log.v("sync", "loading local keystore");
				TrustManagerFactory.loadLocalKeystore(context);
			}

			Session session = ImapClient.getDefaultImapSession(
					settings.getPort(), settings.getUseSSL());
			server = ImapClient.openServer(session, settings.getHost(),
					settings.getUsername(), settings.getPassword());

			StatusHandler.writeStatus(R.string.fetching_messages);

			// Numbers in comments and messages reference Gargan's Algorithm and
			// the wiki

			// 1. retrieve list of all imap message headers
			sourceFolder = server.getFolder(handler.getDefaultFolderName());
			sourceFolder.open(Folder.READ_WRITE);
			Message[] msgs = sourceFolder.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.CONTENT_INFO);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.ENVELOPE);
			sourceFolder.fetch(msgs, fp);

			LocalCacheProvider cache = handler.getLocalCacheProvider();
			Set<Integer> processedEntries = new HashSet<Integer>(
					(int) (msgs.length * 1.2));
			final String processMessageFormat = this.context.getResources()
					.getString(R.string.processing_message_format);
			final StatusEntry status = handler.getStatus();

			for (Message m : msgs)
			{
				if (isStopping()) return;

				if (m.getFlags().contains(Flag.DELETED))
				{
					Log.d("sync", "Found deleted message, continue");
					continue;
				}

				SyncContext sync = new SyncContext();
				try
				{
					sync.setMessage(m);

					StatusHandler.writeStatus(String.format(
							processMessageFormat, status.incrementItems(),
							msgs.length));

					// 2. check message headers for changes
					String subject = sync.getMessage().getSubject();
					Log.d("sync", "2. Checking message " + subject);

					// 5. fetch local cache entry
					sync.setCacheEntry(cache.getEntryFromRemoteId(subject));

					if (sync.getCacheEntry() == null)
					{
						Log.i("sync", "6. found no local entry => save");
						handler.createLocalItemFromServer(session,
								sourceFolder, sync);
						status.incrementLocalNew();
						if (sync.getCacheEntry() == null)
						{
							Log.w("sync",
									"createLocalItemFromServer returned a null object! See Logfile for parsing errors");
						}

					}
					else
					{
						Log.d("sync",
								"7. compare data to figure out what happened");

						boolean cacheIsSame = false;
						if (settings.getCreateRemoteHash())
						{
							cacheIsSame = handler.isSameRemoteHash(
									sync.getCacheEntry(), sync.getMessage());
						}
						else
						{
							cacheIsSame = handler.isSame(sync.getCacheEntry(),
									sync.getMessage());
						}

						// if (CacheEntry.isSame(sync.getCacheEntry(),
						// sync.getMessage()) && !DBG_REMOTE_CHANGED)
						if (cacheIsSame && !DBG_REMOTE_CHANGED)
						{
							Log.d("sync", "7.a/d cur=localdb");
							if (handler.hasLocalItem(sync))
							{
								Log.d("sync",
										"7.a check for local changes and upload them");
								if (handler.hasLocalChanges(sync)
										|| DBG_LOCAL_CHANGED)
								{
									Log.i("sync",
											"local changes found: updating ServerItem from Local");
									handler.updateServerItemFromLocal(session,
											sourceFolder, sync);
									status.incrementRemoteChanged();
								}
							}
							else
							{
								Log.i("sync",
										"7.d entry missing => delete on server");
								handler.deleteServerItem(sync);
								status.incrementRemoteDeleted();
							}
						}
						else
						{
							Log.d("sync",
									"7.b/c check for local changes and \"resolve\" the conflict");
							if (handler.hasLocalChanges(sync))
							{
								Log.i("sync",
										"7.c local changes found: conflicting, updating local item from server");
								status.incrementConflicted();
							}
							else
							{
								Log.i("sync", "7.b no local changes found:"
										+ " updating local item from server");
							}
							handler.updateLocalItemFromServer(sync);
							status.incrementLocalChanged();
						}
					}
				}
				catch (SyncException ex)
				{
					Log.e("sync", ex.toString());
					status.incrementErrors();
				}
				if (sync.getCacheEntry() != null)
				{
					Log.d("sync", "8. remember message as processed (item id="
							+ sync.getCacheEntry().getLocalId() + ")");
					processedEntries.add(sync.getCacheEntry().getLocalId());
				}
			}

			// 9. for all unprocessed local items
			// 9.a upload/delete
			Log.d("sync", "9. process unprocessed local items");

			Set<Integer> localIDs = handler.getAllLocalItemsIDs();
			if (localIDs == null) throw new SyncException("getAllLocalItems",
					"cr.query returned null");
			int currentLocalItemNo = 1;
			int itemsCount = localIDs.size();
			try
			{
				final String processItemFormat = this.context.getResources()
				.getString(R.string.processing_item_format);

				for (int localId : localIDs)
				{
					if (isStopping()) return;					

					Log.d("sync", "9. processing #" + localId);

					StatusHandler.writeStatus(String.format(processItemFormat,
							currentLocalItemNo++, itemsCount));

					if (processedEntries.contains(localId))
					{
						// Log.d("sync",
						// "9.a already processed from server: skipping");
						continue;
					}

					SyncContext sync = new SyncContext();
					sync.setCacheEntry(cache.getEntryFromLocalId(localId));
					if (sync.getCacheEntry() != null)
					{
						Log.i("sync",
								"9.b found in local cache: deleting locally");
						handler.deleteLocalItem(sync);
						status.incrementLocalDeleted();
						status.incrementItems();
						processedEntries.add(localId);
					}
					else
					{
						Log.i("sync",
								"9.c not found in local cache: creating on server");
						handler.createServerItemFromLocal(session,
								sourceFolder, sync, localId);
						status.incrementRemoteNew();
						status.incrementItems();
						processedEntries.add(localId);
					}
				}
			}
			catch (SyncException ex)
			{
				Log.e("sync", ex.toString());
				status.incrementErrors();
			}			
		}
		finally
		{
			if (sourceFolder != null) sourceFolder.close(true);
			if (server != null) server.close();
		}
	}
}
