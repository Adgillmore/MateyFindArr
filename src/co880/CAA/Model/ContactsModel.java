package co880.CAA.Model;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;

/**
 * 
 * @author Ka Lan 
 *A class has methods to query the local contacts and compare them with list of possible registered users.
 */
public class ContactsModel {

	private Context con;
	private ContentResolver conRes;
	private SharedPreferences pref;
	final String[] projection = new String[] { Email.DATA, Email.TYPE };

	/**
	 * @param c c is the context of the parentActivity.
	 */
	public ContactsModel(Context c) {
		con = c;
		conRes = con.getContentResolver();
		pref = con.getSharedPreferences("caaPref", 1);
	}

	/**
	 * @return gmails gmails is the list of contacts on your phone that are gmail addresses.
	 */
	public HashSet<String> returnLocalGmails() {
		HashSet<String> gmails = new HashSet<String>();
		Cursor phones = conRes.query(Email.CONTENT_URI, projection, null, null,
				null);
		while (phones.moveToNext()) {
			int emailColumn = phones.getColumnIndex(Email.DATA);
			String email = phones.getString(emailColumn).toLowerCase();
			String whatever = pref.getString("email", null);
			if ((email.contains("gmail") || email.contains("googlemail")) && (!email.equals((whatever).toLowerCase()))) {
				gmails.add(email);
			}
		}
		return gmails;
	}

	/**
	 * @param regUsers regUsers is a ArrayList of registered users.
	 * @return newGmails newGmails a new list which compares the local gmail addresses with the specified arrayList.
	 */
	public HashSet<String> returnMatched(ArrayList<String> regUsers) {
		HashSet<String> newGmails = returnLocalGmails();
		newGmails.retainAll(regUsers);
		return newGmails;
	}
}
