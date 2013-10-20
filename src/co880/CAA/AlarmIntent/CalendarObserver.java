package co880.CAA.AlarmIntent;

import java.util.HashMap;

import co880.CAA.Model.CalendarModel;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class CalendarObserver extends ContentObserver {

	private int eventID;
	private ActivityPendingManager parentActivity;
	private CalendarModel myCalendar;
	private Context ctx;
	private boolean changeReceived;
	
	
	public CalendarObserver(Handler handler, ActivityPendingManager inActivity, 
			Context inCtx, int inEventID) {
		super(handler);
		eventID = inEventID;
		parentActivity = inActivity;
		myCalendar = new CalendarModel(inCtx);
		changeReceived = false;
	}
	
	public void setObserver(Uri uri) {
		//Add this observer to the data
		myCalendar.getMyResolver().registerContentObserver(uri, false, this);
	}
	
	/* (non-Javadoc)
	 * @see android.database.ContentObserver#onChange(boolean)
	 */
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		//Get event start time
		changeReceived = true;
		HashMap<String, String> eventDetails = myCalendar.getEventDetails(eventID);
		Log.i("CalendarObserver onChange", "EventID: " + eventID);
		if (eventDetails != null) {
			String startTimeString = eventDetails.get("dtstart"); //Could change this to dedicated method for dtstart
			long startTime = Long.parseLong(startTimeString);	
			if (startTime > System.currentTimeMillis()) {
			parentActivity.editPendingIntent(eventID, startTime, myCalendar.isUnderTest());
			}
		} else { //The user has deleted the event
			parentActivity.deletePendingIntent(eventID);
		}
	}

	//Getters and Setters
	
	/**
	 * @return the eventID
	 */
	public int getEventID() {
		return eventID;
	}

	/**
	 * @param eventID the eventID to set
	 */
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}

	/**
	 * @return the parentActivity
	 */
	public ActivityPendingManager getParentActivity() {
		return parentActivity;
	}

	/**
	 * @return the myCalendar
	 */
	public CalendarModel getMyCalendar() {
		return myCalendar;
	}

	public boolean isChangeReceived() {
		return changeReceived;
	}

	public void setChangeReceived(boolean changeReceived) {
		this.changeReceived = changeReceived;
	}

}
