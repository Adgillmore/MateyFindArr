package co880.CAA.Model;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * 
 * @author Adam.
 *This class allows you manager raw contacts which are specifically for our app.
 * Modifed from Android.Developers.com
 *
 */
public class RawContactManager {
	
	public RawContactManager() {
	}
	
	public void deleteRawContact(Context ctx, String account_name) {
		StringBuilder sb = new StringBuilder();
		sb.append(RawContacts.ACCOUNT_TYPE);
		// com.orpheus our identifer.
		sb.append("='com.orpheus' AND ");
		sb.append(RawContacts.ACCOUNT_NAME);
		sb.append("='");
		sb.append(account_name);
		sb.append("'");
		String deleteString = sb.toString();
		ctx.getContentResolver().delete(RawContacts.CONTENT_URI, deleteString, null);
	}
	
	/**
	 * Takes a list of gmails addresses acquired from the phone and removes any that are 
	 * already raw contacts of our account type.
	 * @param ctx
	 * @param potentialContacts
	 * @return
	 */
	public ArrayList<String> checkAndReturnContacts(Context ctx, ArrayList<String> potentialContacts) {
		ArrayList<String> allRawContacts = getRawContacts(ctx);
		potentialContacts.removeAll(allRawContacts);
		return potentialContacts;
	}
	
	/**
	 * Creates raw contacts for each entry in an ArrayList
	 * @param ctx
	 * @param inContacts
	 */
	public void createBulkContacts(Context ctx, ArrayList<String> inContacts) {
		for (int i=0; i<inContacts.size(); i++) {
			insertNewContact(ctx, inContacts.get(i));
		}
	}
		
	/**
	 * Creates a new raw contact for a single entry
	 * @param ctx
	 * @param accountName
	 */
	public void insertNewContact(Context ctx, String accountName) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, "com.orpheus")
				.withValue(RawContacts.ACCOUNT_NAME, accountName) // gmail address
				.build());

		ops.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, "").build()); //Could add their display name here

		try {
			ctx.getContentResolver()
					.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			e.printStackTrace();
		}
	}
	
	// Returns deleted contacts indicated by the DeletedFlag.
	public ArrayList<String> getDeletedContacts(Context ctx) {
		Cursor c2 = ctx.getContentResolver().query(RawContacts.CONTENT_URI,
		          new String[]{RawContacts.ACCOUNT_NAME}, RawContacts.ACCOUNT_TYPE + "= 'com.orpheus' AND " + RawContacts.DELETED + "=1", null, null);
		ArrayList<String> deletedContacts = new ArrayList<String>();
		c2.moveToFirst();
			for(int i=0;i<c2.getCount(); i++) {
				deletedContacts.add(c2.getString(0));
				c2.moveToNext();
			}
			c2.close();
		return deletedContacts;
	}
	
	// Returns all rawContacts which are not deleted.
	public ArrayList<String> getRawContacts(Context ctx) {
		Cursor c1 = ctx.getContentResolver().query(RawContacts.CONTENT_URI,
		          new String[] {RawContacts.ACCOUNT_NAME}, RawContacts.ACCOUNT_TYPE + "= 'com.orpheus' AND " + RawContacts.DELETED + "=0", null, null);
		ArrayList<String> rawContacts = new ArrayList<String>();
		c1.moveToFirst();
			for(int i=0;i<c1.getCount(); i++) {
				rawContacts.add(c1.getString(0));
				c1.moveToNext();
			}
			c1.close();
		return rawContacts;
	}

}