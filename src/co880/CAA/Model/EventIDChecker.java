package co880.CAA.Model;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 
 * @author Adam
 *This class retrieves the eventID from an automatically launched intent
 *and stores it in shared preferences for subsequent app launches until the
 *event has expired.
 */
public class EventIDChecker {

	private final static String TAG = "EventIDChecker";
	private static Intent thisIntent;

	public EventIDChecker () {
	}

	// boolean invalidActivity is used for testing purposes
	public static int getEventID(Activity activity, boolean invalidActivity) {
		SharedPreferences pref = activity.getSharedPreferences("caaPref",
				Activity.MODE_WORLD_READABLE);
		if(invalidActivity) { 
		} else {
			thisIntent = activity.getIntent();
		}
		int eventID = thisIntent.getIntExtra("eventID", -1);
		boolean underTest = thisIntent.getBooleanExtra("underTest", false);

		Log.i(TAG, "EventID: " + eventID);
		if (eventID != -1) {// Activity was started automatically
			// put eventID in prefs

			SharedPreferences.Editor editor = pref.edit();
			editor.putInt("eventID", eventID);
			editor.commit();
			thisIntent.removeExtra("eventID");
			Log.i(TAG, "Removed EventID: " + eventID + " from intent");
		} else {// Activity started manually
			// get eventID from pref
			eventID = pref.getInt("eventID", -1);
			Log.i(TAG, "EventID: " + eventID);
		}

		return eventID;
	}
	
	public void setIntent(Intent intent) {
		thisIntent = intent;
	}
}
