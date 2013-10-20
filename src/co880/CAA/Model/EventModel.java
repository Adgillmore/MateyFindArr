package co880.CAA.Model;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.google.android.gcm.GCMRegistrar;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.MyEventDb;
import co880.CAA.ServerUtils.DeleteLocationData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

/**
 * @author Adam
 * This class checks the expiry time of an event and carries out
 * the mechanics of the session such as fetching the boundary data,
 * monitoring the time left, updating the time display and closing 
 * the session early.
 */
public class EventModel extends ExpiryChecker {

	private SharedPreferences pref;
	private LocationActivity activity;
	private Handler sessionHandler;
	private SessionThread sessionThread;
	private AlertDialog startAlert;
	private AlertDialog endAlert;
	private boolean noDataOnServer;
	private boolean boundarySet;
	private final static String TAG = "EventModel";

	public EventModel(LocationActivity inActivity, int eventID) {
		super(inActivity, eventID);
		activity = inActivity;
		pref = inActivity.getSharedPreferences("caaPref",
				Activity.MODE_WORLD_READABLE);
		boundarySet = false;
		//intentService = new Intent(inActivity, LocationService.class);
	}
	
	/**
	 * @author Adam
	 * Checks expiry time and toggles the boolean field
	 * eventActive.
	 */
	public void checkExpiryTime() {
		// Check if event has expired
		Log.i("EventModel checkExpiry check db", "EventID: " + getEventID());
		if (getEndTime() > System.currentTimeMillis()) {// event running
			setEventActive(true);
		} else { // event expired or doesn't exist
			setEventActive(true);
		}
	}

	/**
	 * @author Adam
	 * Finds the boundary data for this event and toggles
	 * the boolean field boundarySet.
	 */
	public void checkBoundary() {
		// query database for boundary info
		MyEventDb eventDb = new MyEventDb(activity);
		eventDb.openRead();
		Cursor cursor = eventDb.queryEvent(getEventID());
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor.getInt(2) > 0) {
				boundarySet = true;
				activity.setBoundaryCentre(cursor.getInt(0), cursor.getInt(1));
				activity.setBoundaryRadius((float) cursor.getInt(2));
			} 
			
		} else {
			boundarySet = false;
		}
		cursor.close();
		eventDb.close();
	}

	/**
	 * @Adam and Dan
	 * Receives the timer count from the thread and updates
	 * the user info bar.
	 * @param difference
	 */
	public void updateTimeDisplay(long difference) {
		TimeZone timezone = TimeZone.getDefault();
		if (timezone.inDaylightTime(new Date(getEndTime()))) {
			difference = difference - timezone.getDSTSavings();
		}
		String timeDifference = (Utils.convertEpochToTimeFormat(difference));
		String timeDifferenceSubString = timeDifference.substring(0, 5);
		((LocationActivity) activity)
				.setTimeRemainingText(timeDifferenceSubString);
	}

	public boolean sessionHasNotExpired() {
		if ((getEndTime() > System.currentTimeMillis())) {
			return true;
		}
		return false;
	}

	public HashMap<String, String> getAttendeeList() {
		HashMap<String, String> attendees = new HashMap<String, String>();
		if (getEventID() != -1) {
			attendees = CalendarModel.getAttendees(activity, getEventID());
		}
		return attendees;
	}

	/**
	 * @author Adam
	 * Toggles the server data 'dirty bit', 
	 * deletes location data, 
	 * removes the eventID from preferences.
	 * @param activity The activity to be closed
	 */
	public void closeSession(Activity activity) {
		setEventActive(false);
		DeleteLocationData delete = new DeleteLocationData(activity);
		delete.execute(pref.getString("email", null));
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("noDataOnServer", true);
		editor.commit();
		Log.i(TAG, "Deleted location data for EventID: " + getEventID());
		editor.remove("eventID");
		editor.commit();
		Log.i("EventModel removed from prefs", "EventID: " + super.getEventID());
	}

	public void finishEarly(Activity activity) {
		if (returnEndTimeMap() != null) {
			returnEndTimeMap()
					.put("dtend", Long.toString(System.currentTimeMillis()));
			closeSession(activity);
			checkEventIsActive();
		}
	}

	public void runSessionThread(LocationService l) {
		if (sessionHandler == null) {
			sessionHandler = new SessionHandler(this, l);
			sessionThread = new SessionThread(sessionHandler, getEndTime());
			sessionThread.start();
			return;
		}

		if (sessionThread.getState() != Thread.State.TERMINATED) {
			// Thread is already there
		} else {
			// Create new thread
			sessionThread = new SessionThread(sessionHandler, getEndTime());
			sessionThread.start();
		}

	}

	public void checkEventIsActive() {
		if (!isEventActive()) {
			activity.finish();
		} else {
			// Do nothing
		}
	}

	public Handler getSessionHandler() {
		return sessionHandler;
	}

	public SessionThread getSessionThread() {
		return sessionThread;
	}

	public boolean getNoDataOnServer() {
		return noDataOnServer;
	}

	public void setNoDataOnServer(boolean noDataOnServer) {
		this.noDataOnServer = noDataOnServer;
	}

	public boolean hasBoundary() {
		return boundarySet;
	}

	public void setBoundary(boolean b) {
		boundarySet = b;
	}

	public boolean getBoundarySet() {
		return boundarySet;
	}

	public LocationActivity getActivity() {
		return activity;
	}

}