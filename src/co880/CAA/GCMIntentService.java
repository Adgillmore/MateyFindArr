package co880.CAA;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.SyncManager;
import co880.CAA.ServerUtils.JSONParser;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * 
 * @authors: Adam and Ka lan.
 * This class receives messages from the GCM servers and acts accordingly.
 *
 */

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCMIntentService";
	private SyncManager syncMan;

	@Override
	protected void onError(Context arg0, String arg1) {
		
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		String JSONEvent = arg1.getStringExtra("message");
		Log.i(TAG, "received message " + JSONEvent);
		
		// @Adam: The sync is forced so the user will have received the invite and have it inserted into their calendars.
		syncMan = new SyncManager();
		syncMan.forceSync(arg0);
			
		// @Adam: Details are extracted from the JSONObject.
		JSONParser myParser = new JSONParser();
		ArrayList<String> event = myParser.parseEvent(JSONEvent, "eventObject", new String[] {"calendar_id", "title", 
				"description", "dtstart", "dtend", "eventTimezone", "isUpdate"});
		ArrayList<String> attendees = myParser.parseAttendees(JSONEvent);
		String calID = event.get(0);
		String title = event.get(1);
		String desc = event.get(2);
		String dtstart = event.get(3);
		String dtend = event.get(4);
		String timeZone = event.get(5);
		String isUpdate = event.get(6);
		
		CalendarModel model = new CalendarModel(arg0);
		
		//@Adam: We enter a loop to try and find a match between the recieved event to match with the synced one on the calendar.
		//After a match is found we determine if it is a new event or an update.
		String eventID = null;
		eventID = model.compareEvents(dtstart, dtend, title);// in case of fast sync
		int counter = 0;
		while (eventID == null && counter <20) {// in case of slow sync
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventID = model.compareEvents(dtstart, dtend, title);
			counter++;
		}
		while (eventID == null && counter <30) {// in case of very slow sync
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventID = model.compareEvents(dtstart, dtend, title);
			counter++;
		}
		if (eventID != null && isUpdate.equals("true")) {
			//@Ka lan: update the pendingIntent.
			model.setUpdateAlarm(Integer.parseInt(eventID), dtstart);
			Log.i("GCMIntentService updated event", "EventID: " +eventID);
		} else if (eventID != null){
			Log.i("GCMIntentService compare event", "EventID: " +eventID);
			//@Ka lan: Set new pendingIntent.
			model.setAlarm(Integer.parseInt(eventID), dtstart);
		}
		//@Ka lan: Add boundary and details to local database.
		if (isUpdate.equals("false")){
		ArrayList<String> boundary = myParser.parseEvent(JSONEvent, "boundaryObject", new String[] {"radius", "latitude", "longitude"});
		int radius = Integer.parseInt(boundary.get(0));
		int lat = Integer.parseInt(boundary.get(1));
		int lon = Integer.parseInt(boundary.get(2));
		model.setSuccessfulInsert(true);
		model.addToDB(Integer.parseInt(eventID), radius, lat, lon);
		}
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {

		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
