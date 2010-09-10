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

package at.dasz.KolabDroid.ContactsContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import at.dasz.KolabDroid.Utils;
import at.dasz.KolabDroid.Provider.LocalCacheProvider;
import at.dasz.KolabDroid.Settings.Settings;
import at.dasz.KolabDroid.Sync.AbstractSyncHandler;
import at.dasz.KolabDroid.Sync.CacheEntry;
import at.dasz.KolabDroid.Sync.SyncContext;
import at.dasz.KolabDroid.Sync.SyncException;

public class SyncContactsHandler extends AbstractSyncHandler
{
	//private static final String[]		PEOPLE_ID_PROJECTION	= new String[] { People._ID };
	
	/*
	private static final String[]		PHONE_PROJECTION		= new String[] {
			Contacts.Phones.TYPE, Contacts.Phones.NUMBER		};
	private static final String[]		EMAIL_PROJECTION		= new String[] {
			Contacts.ContactMethods.TYPE, Contacts.ContactMethods.DATA };
	//private static final String[]		PEOPLE_NAME_PROJECTION	= new String[] { People.NAME };
	
	private static final String[]	CONTACT_NAME_PROJECTION = new String[] { ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME };
	
	
	private static final String[]		ID_PROJECTION			= new String[] { "_id" };
	private static final String			EMAIL_FILTER			= Contacts.ContactMethods.KIND
																		+ "="
																		+ Contacts.KIND_EMAIL;
*/
	
	private final String				defaultFolderName;
	private final LocalCacheProvider	cacheProvider;
	private final ContentResolver		cr;

	public SyncContactsHandler(Context context)
	{
		super(context);
		Settings s = new Settings(context);
		defaultFolderName = s.getContactsFolder();
		cacheProvider = new LocalCacheProvider.ContactsCacheProvider(context);
		cr = context.getContentResolver();
		status.setTask("Contacts");
	}

	public String getDefaultFolderName()
	{
		return defaultFolderName;
	}

	public LocalCacheProvider getLocalCacheProvider()
	{
		return cacheProvider;
	}

	public Cursor getAllLocalItemsCursor()
	{
		//return cr.query(People.CONTENT_URI, PEOPLE_ID_PROJECTION, null, null,
//				null);
		return cr.query(ContactsContract.Data.CONTENT_URI,
				new String[]{ContactsContract.RawContacts.Data.RAW_CONTACT_ID}, null, null, null);
	}

	public int getIdColumnIndex(Cursor c)
	{
		//return c.getColumnIndex(People._ID);
		return c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
	}

	@Override
	protected void updateLocalItemFromServer(SyncContext sync, Document xml) throws SyncException
	{
		Contact contact = (Contact) sync.getLocalItem();
		if (contact == null)
		{
			contact = new Contact();
		}
		Element root = xml.getDocumentElement();

		contact.setUid(Utils.getXmlElementString(root, "uid"));

		Element name = Utils.getXmlElement(root, "name");
		if (name != null)
		{
			//contact.setFullName(Utils.getXmlElementString(name, "full-name"));
			String fullName = Utils.getXmlElementString(name, "full-name");
			if(fullName != null)
			{
				String[] names = fullName.split(" ");
				if(names.length == 2)
				{
					contact.setGivenName(names[0]);
					contact.setFamilyName(names[1]);
				}
			}
		}
		contact.getContactMethods().clear();
		NodeList nl = Utils.getXmlElements(root, "phone");
		for (int i = 0; i < nl.getLength(); i++)
		{
			ContactMethod cm = new PhoneContact();
			cm.fromXml((Element) nl.item(i));
			contact.getContactMethods().add(cm);
		}
		nl = Utils.getXmlElements(root, "email");
		for (int i = 0; i < nl.getLength(); i++)
		{
			ContactMethod cm = new EmailContact();
			cm.fromXml((Element) nl.item(i));
			contact.getContactMethods().add(cm);
		}

		//create hash of mail and attach to contact		
		String docText = Utils.getXml(root);
		byte[] remoteHash = Utils.sha1Hash(docText);		
		contact.setRemoteHash(remoteHash);
		
		sync.setCacheEntry(saveContact(contact));
	}

	@Override
	protected void updateServerItemFromLocal(SyncContext sync, Document xml) throws SyncException, MessagingException
	{
		Contact source = getLocalItem(sync);
		CacheEntry entry = sync.getCacheEntry();

		entry.setLocalHash(source.getLocalHash());
		final Date lastChanged = new Date();
		entry.setRemoteChangedDate(lastChanged);

		writeXml(xml, source, lastChanged);
	}

	private void writeXml(Document xml, Contact source, final Date lastChanged)
	{
		Element root = xml.getDocumentElement();

		Utils.setXmlElementValue(xml, root, "last-modification-date", Utils
				.toUtc(lastChanged));

		Utils.setXmlElementValue(xml, root, "uid", source.getUid());

		Element name = Utils.getOrCreateXmlElement(xml, root, "name");
		Utils.setXmlElementValue(xml, name, "full-name", source.getFullName());
		Utils.setXmlElementValue(xml, name, "given-name", source.getGivenName());
		Utils.setXmlElementValue(xml, name, "last-name", source.getFamilyName());

		Utils.deleteXmlElements(root, "phone");
		Utils.deleteXmlElements(root, "email");

		for (ContactMethod cm : source.getContactMethods())
		{
			cm.toXml(xml, root, source.getFullName());
		}
	}

	@Override
	protected String writeXml(SyncContext sync)
			throws ParserConfigurationException, SyncException, MessagingException
	{
		Contact source = getLocalItem(sync);
		CacheEntry entry = sync.getCacheEntry();

		entry.setLocalHash(source.getLocalHash());
		final Date lastChanged = new Date();
		entry.setRemoteChangedDate(lastChanged);
		final String newUid = getNewUid();
		entry.setRemoteId(newUid);
		source.setUid(newUid);
		
		Document xml = Utils.newDocument("contact");
		writeXml(xml, source, lastChanged);

		return Utils.getXml(xml);
	}

	@Override
	protected String getMimeType()
	{
		return "application/x-vnd.kolab.contact";
	}

	public boolean hasLocalItem(SyncContext sync) throws SyncException, MessagingException
	{
		return getLocalItem(sync) != null;
	}

	public boolean hasLocalChanges(SyncContext sync) throws SyncException, MessagingException
	{
		CacheEntry e = sync.getCacheEntry();
		Contact contact = getLocalItem(sync);;
		String entryHash = e.getLocalHash();
		String contactHash = contact != null ? contact.getLocalHash() : "";
		return !entryHash.equals(contactHash);
	}

	@Override
	public void deleteLocalItem(int localId)
	{		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    	
		ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).
    	withSelection(ContactsContract.RawContacts._ID + "=?", new String[]{String.valueOf(localId)}).
    	build());
		
		//remove all phone numbers and email addresses as well
		ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).
				withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{String.valueOf(localId)}).
				build());
		
		//TODO: delete entry in "contact" table?
		
		try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
        	Log.e("EE", e.toString());
        }
		
	}

	private CacheEntry saveContact(Contact contact) throws SyncException
	{
		Uri uri = null;
		
		String name = contact.getFullName();
		String firstName = contact.getGivenName();
		String lastName = contact.getFamilyName();
		
		String email = "";
		String phone = "";
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		if (contact.getId() == 0)
		{	
			ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
	                .build());
	        
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                .withValue(ContactsContract.Data.MIMETYPE,
	                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
	                .build());

			for (ContactMethod cm : contact.getContactMethods())
			{
				if(cm instanceof EmailContact)
				{
					email = cm.getData();
					
					ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			                .withValue(ContactsContract.Data.MIMETYPE,
			                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
			                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email).
			                withValue(ContactsContract.CommonDataKinds.Email.TYPE, cm.getType()).build());
				}
				
				if(cm instanceof PhoneContact)
				{
					phone = cm.getData();
					
					ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			                .withValue(ContactsContract.Data.MIMETYPE,
			                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
			                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
			                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, cm.getType())
			                .build());
				}
				
				Log.i("II", cm.toString() + cm.getClass());
			}
		}
		else
		{
			Log.i("II", "Contact already in Android book");
			
			Uri updateUri = ContactsContract.Data.CONTENT_URI;
			
			//fetch contact with rawID and adjust its values
			
			//first remove stuff that is in addressbook
			//Cursor personCursor = null, phoneCursor = null, emailCursor = null;
			Cursor phoneCursor = null, emailCursor = null;
			//phone
			{
				String w = ContactsContract.Data.RAW_CONTACT_ID+"='"+contact.getId()+"' AND " +
				ContactsContract.Contacts.Data.MIMETYPE + " = '"+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE+"'";
				
				//Log.i("II", "w: " + w);
				
				phoneCursor = cr.query(updateUri, null, w, null, null);
				
				if (phoneCursor == null) throw new SyncException(
						"EE", "cr.query returned null");
				
				if(phoneCursor.getCount() > 0) // otherwise no phone numbers
				{				
					if (!phoneCursor.moveToFirst()) return null;
					int idCol = phoneCursor.getColumnIndex(ContactsContract.Data._ID);
					do
					{						
						ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).
								withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(phoneCursor.getInt(idCol))}).
								build());						
					 }while (phoneCursor.moveToNext());
				}
			}
			
			//mail
			{
				String w = ContactsContract.Data.RAW_CONTACT_ID+"='"+contact.getId()+"' AND " +
				ContactsContract.Contacts.Data.MIMETYPE + " = '"+ ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE+"'";
				
				emailCursor = cr.query(updateUri, null, w, null, null);
				
				if (emailCursor == null) throw new SyncException(
						"EE", "cr.query returned null");
				
				if(emailCursor.getCount() > 0) // otherwise no email addresses
				{
					if (!emailCursor.moveToFirst()) return null;								
					int idCol = emailCursor.getColumnIndex(ContactsContract.Data._ID);
					do
					{
						ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).
								withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(emailCursor.getInt(idCol))}).
								build());						
					}while (emailCursor.moveToNext());
				}
			}
			
			//insert again
			
			for (ContactMethod cm : contact.getContactMethods())
			{
				if(cm instanceof EmailContact)
				{
					email = cm.getData();					
					ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.getId())
			                .withValue(ContactsContract.Data.MIMETYPE,
			                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
			                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email).
			                withValue(ContactsContract.CommonDataKinds.Email.TYPE, cm.getType()).build());				
				}
				
				if(cm instanceof PhoneContact)
				{
					phone = cm.getData();
					
					ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.getId())
			                .withValue(ContactsContract.Data.MIMETYPE,
			                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
			                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
			                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, cm.getType())
			                .build());
				}
				
				Log.i("II", cm.toString() + cm.getClass());
			}
			
			if (phoneCursor != null) phoneCursor.close();
			if (emailCursor != null) emailCursor.close();
		}
		
		//Log.i("II", "Creating contact: " + firstName + " " + lastName);
        try {
            ContentProviderResult[] results = cr.applyBatch(ContactsContract.AUTHORITY, ops);
            //store the first result: it contains the uri of the raw contact with its ID
            if(contact.getId() == 0)
            {
            	uri = results[0].uri;
            }
            else
            {
            	uri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contact.getId());
            }
            
            for(ContentProviderResult cpr : results)
            {
            	Log.i("II", cpr.uri.toString());
            }
        } catch (Exception e) {	
            // Log exception
            Log.e("EE","Exception encountered while inserting contact: " + e);
        }

		CacheEntry result = new CacheEntry();
		result.setLocalId((int) ContentUris.parseId(uri));
		result.setLocalHash(contact.getLocalHash());
		result.setRemoteId(contact.getUid());
		result.setRemoteHash(contact.getRemoteHash());
		return result;
	}

	private Contact getLocalItem(SyncContext sync) throws SyncException,
			MessagingException
	{
		if (sync.getLocalItem() != null) return (Contact) sync.getLocalItem();

		Uri uri = ContactsContract.Data.CONTENT_URI;

		Cursor personCursor = null, phoneCursor = null, emailCursor = null;
		try
		{			
			String where = ContactsContract.Data.RAW_CONTACT_ID+"='"+sync.getCacheEntry().getLocalId()+"' AND " +
			ContactsContract.Contacts.Data.MIMETYPE + " = '"+ ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE+"'";
			
			Log.i("II", "where: " + where);
			
			personCursor = cr.query(uri, null, where, null, null);
			
			if (personCursor == null) throw new SyncException(
					getItemText(sync), "cr.query returned null");
			if (!personCursor.moveToFirst()) return null;
			
			//int idx = personCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
			int idxFirst = personCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
			int idxLast = personCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
			
			String firstName = personCursor.getString(idxFirst);
			String lastName = personCursor.getString(idxLast);
			
			String name = firstName + " " + lastName;
			
			Log.i("II", "Cursor Line" +name);
						
			Contact result = new Contact();
			result.setId(sync.getCacheEntry().getLocalId());
			result.setUid(sync.getCacheEntry().getRemoteId());
					
			//result.setFullName(personCursor.getString(nameIdx));
			result.setGivenName(firstName);
			result.setFamilyName(lastName);

			//phone
			{
				String w = ContactsContract.Data.RAW_CONTACT_ID+"='"+sync.getCacheEntry().getLocalId()+"' AND " +
				ContactsContract.Contacts.Data.MIMETYPE + " = '"+ ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE+"'";
				
				phoneCursor = cr.query(uri, null, w, null, null);
				
				if (phoneCursor == null) throw new SyncException(
						getItemText(sync), "cr.query returned null");
				
				if(phoneCursor.getCount() > 0) // otherwise no phone numbers
				{
				
					if (!phoneCursor.moveToFirst()) return null;
					
					int numberIdx = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int typeIdx = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);			
					
					do
					{
						PhoneContact pc = new PhoneContact();
						pc.setData(phoneCursor.getString(numberIdx));
						pc.setType(phoneCursor.getInt(typeIdx));
						result.getContactMethods().add(pc);
					 }while (phoneCursor.moveToNext());
				}
			}
			
			//mail
			{
				String w = ContactsContract.Data.RAW_CONTACT_ID+"='"+sync.getCacheEntry().getLocalId()+"' AND " +
				ContactsContract.Contacts.Data.MIMETYPE + " = '"+ ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE+"'";
				
				emailCursor = cr.query(uri, null, w, null, null);
				
				if (emailCursor == null) throw new SyncException(
						getItemText(sync), "cr.query returned null");
				
				if(emailCursor.getCount() > 0) // otherwise no email addresses
				{
					if (!emailCursor.moveToFirst()) return null;
					
					int dataIdx = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
					//int typeIdx = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);			
					
					do
					{
						EmailContact pc = new EmailContact();
						pc.setData(emailCursor.getString(dataIdx));
						//pc.setType(emailCursor.getInt(typeIdx));
						result.getContactMethods().add(pc);
					}while (emailCursor.moveToNext());
				}
			}

			sync.setLocalItem(result);
			return result;
		}
		finally
		{
			if (personCursor != null) personCursor.close();
			if (phoneCursor != null) phoneCursor.close();
			if (emailCursor != null) emailCursor.close();
		}
	}

	private String getNewUid()
	{
		// Create Application and Type specific id
		// kd == Kolab Droid, ct = contact
		return "kd-ct-" + UUID.randomUUID().toString();
	}

	@Override
	protected String getMessageBodyText(SyncContext sync) throws SyncException, MessagingException
	{
		Contact contact = getLocalItem(sync);
		StringBuilder sb = new StringBuilder();

		String fullName =contact.getFullName(); 
		sb.append(fullName == null ? "(no name)" : fullName);
		sb.append("\n");
		sb.append("----- Contact Methods -----\n");
		for (ContactMethod cm : contact.getContactMethods())
		{
			sb.append(cm.getData());
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public String getItemText(SyncContext sync) throws MessagingException
	{
		if (sync.getLocalItem() != null)
		{
			Contact item = (Contact) sync.getLocalItem();
			return item.getFullName();
		}
		else
		{
			return sync.getMessage().getSubject();
		}
	}
}
