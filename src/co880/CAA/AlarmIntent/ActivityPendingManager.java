package co880.CAA.AlarmIntent;

import java.util.HashMap;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Activities.LocationActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import co880.CAA.AlarmIntent.CalendarObserver;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Original author Ka Lan, additional contributor Adam.
 *  This class manages the pendingIntents to launch the CAAActivity class.
 *  Pro Android 4 was used as an reference for this class, Chapter 20: Exploring the Alarm Manager.
 */

public class ActivityPendingManager{
	
	private Context con;
	private AlarmManager alarmManager;
	//HashMap used to manage the current pending intent within the lifetime of the stated context. To view the pending intents you can also use ADB shell.
	private HashMap<Integer, PendingIntent> pendingIntentsHashMap = new HashMap<Integer, PendingIntent>();
	private Long lastTimestampUsed;
	
	public ActivityPendingManager(Context c) {
		con = c;
	    alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
	    lastTimestampUsed = 0L;
	}
	
	/**
	 * 
	 * @param eventId of the event inserted.
	 * @param timestamp in epoch form.
	 * @param underTest only put in for testing purposes.
	 */
	public void sendPendingIntent(int eventId, Long timestamp, boolean underTest) {
		//@Adam: changed to CAAActivity due to the way our session ends.
		Intent intent = new Intent(con, CAAActivity.class);
		intent.putExtra("eventID", eventId);
		intent.putExtra("underTest", underTest);
		Log.i("LAPM setIntent", "EventID: " + eventId);
		intent.putExtra("newLaunch", true);
		//@Adam: changed to FLAG_UPDATE_CURRENT.
		PendingIntent pendingIntent = PendingIntent.getActivity(con.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
		setLastTimestampUsed(timestamp);
		getPendingIntentsHashMap().put(eventId, pendingIntent);
		Log.i("LAPM setHashMap", "EventID: " + eventId);
	}
	
	/**
	 * 
	 * @param eventId
	 */
	public void deletePendingIntent(int eventId) {
		PendingIntent tempPendingIntent = getPendingIntentsHashMap().get(eventId);
		alarmManager.cancel(tempPendingIntent);
		if (tempPendingIntent != null) {
		//@Adam: added this to fix a bug where deleted events still triggered the observer and caused a NPE.
		tempPendingIntent.cancel();
		}
		pendingIntentsHashMap.remove(eventId);
	}
	
	/**
	 * 
	 * @param eventId
	 * @param newTimeStamp
	 * @param underTest
	 */
	public void editPendingIntent(int eventId, Long newTimeStamp, boolean underTest) {
		deletePendingIntent(eventId);
		sendPendingIntent(eventId, newTimeStamp, underTest);
	}
	
	public HashMap<Integer, PendingIntent> getPendingIntentsHashMap() {
		return pendingIntentsHashMap;
	}
	
	public void setPendingIntentsHashMap(HashMap<Integer, PendingIntent> pendingIntentsHashMap) {
		this.pendingIntentsHashMap = pendingIntentsHashMap;
	}
	
	public Long getLastTimestampUsed() {
		return lastTimestampUsed;
	}
	
	public void setLastTimestampUsed(Long lastTimestampUsed) {
		this.lastTimestampUsed = lastTimestampUsed;
	}
}
