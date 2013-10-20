package co880.CAA.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Button;

/**
 * 
 * @author Ka Lan This class has a method which can return a google calendar
 *         account number stored on the sql lite database on your phone. Useful
 *         for further queries.
 */

public class RetrieveGoogleCalendarIdNo {

	private Cursor cursor;
	private ContentResolver contentResolver;
	private Context con;

	// Context the base application and the email is the google account
	// preferably from the sharedPreferences.
	public RetrieveGoogleCalendarIdNo(Context c) {
		con = c;
		contentResolver = con.getContentResolver();
	}

	public String getGoogleAccount(String googleEmail) {
		if (googleEmail != null) {

			cursor = contentResolver.query(
					Uri.parse("content://com.android.calendar/calendars"),
					(new String[] { "_id, ownerAccount" }), null, null, null);

			while (cursor.moveToNext()) {
				String temp = cursor.getString(1);
				if (temp != null) {
					if (temp.contentEquals(googleEmail)) {
						return cursor.getString(0);
					}
				}

			}
			return null;
		}
		return null;
	}
	
	// Writes the id to SharedPreferences.
	public void writeCalendarAcc(String googleEmail) {
		SharedPreferences sharedPref = con.getSharedPreferences("caaPref", Context.MODE_WORLD_READABLE);
		String calId = getGoogleAccount(googleEmail);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("calendarId", calId);
		editor.commit();
	}
}
