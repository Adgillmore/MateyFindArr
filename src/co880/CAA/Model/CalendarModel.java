package co880.CAA.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import co880.CAA.AlarmIntent.CalendarObserver;
import co880.CAA.AlarmIntent.ActivityPendingManager;
import co880.CAA.ServerUtils.SendEventThread;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * @author Adam 
 * This class contains methods to insert or query data in the
 * android calendar using a content resolver.
 * 
 */
public class CalendarModel {

	// Fields
	private Cursor cursor;
	private Context ctx;
	private ContentResolver myResolver;
	private String eventTitle;
	private String eventStart;
	private String eventEnd;
	private String eventDescription;
	private TimeZone tZone;
	private boolean successfulInsert;
	private CalendarObserver calObserver;
	private Uri newEvent;
	private Uri newAttendees;
	private boolean hasAttendees;
	private boolean eventCreationFailed;
	private MyEventDb database;
	private Handler sendEventHandler;
	private SendEventThread sendEventThread;
	private SharedPreferences prefs;
	private boolean underTest;
	private ActivityPendingManager manager;
	private SyncManager syncMan;

	// Constructor
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("static-access")
	public CalendarModel(Context inCtx) {
		ctx = inCtx;
		cursor = null;
		myResolver = ctx.getContentResolver();
		tZone = TimeZone.getDefault();
		successfulInsert = false;
		newEvent = null;
		newAttendees = null;
		eventCreationFailed = true;
		database = new MyEventDb(inCtx);
		prefs = ctx.getSharedPreferences("caaPref", ctx.MODE_WORLD_READABLE);

	}

	// Methods

	/**
	 * @author Adam
	 * This method inserts data into the event table, attendees table, local
	 * SQLite database and sends the information on to other users if required.
	 * 
	 * @param calendar
	 *            String containing the id of the users Google calendar.
	 * @param title
	 *            String for the title of the event.
	 * @param description
	 *            String for the description of the event.
	 * @param startTime
	 *            String for the start time in milliseconds since epoch.
	 * @param endTime
	 *            String for the start time in milliseconds since epoch.
	 * @param attendees
	 *            String array of gmail addresses for attendees.
	 */
	public void insertNewEvent(String calendarAcc, String title,
			String description, String startTime, String endTime,
			ArrayList<String> attendees, int radius, int lat, int lon) {
		addEvent(calendarAcc, title, description, startTime, endTime);
		int eventID = findLastEventID(calendarAcc);
		Log.i("CalendarModel insert event", "EventID: " + eventID);
		addAttendees(attendeesPresent(attendees), eventID, attendees);
		if (eventID > -1) {
			setAlarm(eventID, startTime);
		}
		checkForSuccess();
		if (hasAttendees && successfulInsert) {
			boolean isUpdate = false;
			sendToAttendees(calendarAcc, title, description, startTime,
					endTime, attendees, radius, lat, lon, isUpdate);
			syncMan = new SyncManager();
			syncMan.forceSync(ctx);
		}
		addToDB(eventID, radius, lat, lon);

	}

	/**
	 * @author Adam
	 * This method adds data to the event table and sets the newEvent field
	 * with the URI of the event.
	 * @param calendarAcc
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 */
	public void addEvent(String calendarAcc, String title, String description,
			String startTime, String endTime) {
		ContentValues cv = new ContentValues();
		if (calendarAcc != null && startTime != null && endTime != null
				&& tZone != null) {
			cv.put("calendar_id", calendarAcc);// Required
			cv.put("title", title);// Optional
			cv.put("description", description);// Optional
			cv.put("dtstart", startTime); // Required
			cv.put("dtend", endTime); // Required
			cv.put("eventTimezone", tZone.getID());// Required for ICS
			if (hasAttendees) {
				cv.put("hasAttendeeData", "1");
				//cv.put("guestsCanInviteOthers", "0");
				//cv.put("guestsCanModify", "0");
			} else {
				cv.put("hasAttendeeData", "0");
			}
			newEvent = myResolver.insert(
					Uri.parse("content://com.android.calendar/events"), cv);
			eventCreationFailed = false;
		} else {
			eventCreationFailed = true;
		}
	}

	/**
	 * @author Adam
	 * This method adds data to the attendees table and sets the newAttendees field
	 * with the URI of the data.
	 * @param hasAttendees
	 * @param eventID
	 * @param attendees
	 */
	public void addAttendees(boolean hasAttendees, int eventID,
			ArrayList<String> attendees) {
		if (hasAttendees && !eventCreationFailed) {
			for (String string : attendees) {
				ContentValues cv2 = new ContentValues();
				cv2.put("event_id", eventID);
				cv2.put("attendeeEmail", string);
				cv2.put("attendeeStatus", "0");// undecided
				newAttendees = myResolver.insert(
						Uri.parse("content://com.android.calendar/attendees"),
						cv2);
			}
		} else {
			// Do nothing
		}
	}
	
	/**
	 * @author Adam
	 * This method edits an existing row in the event table
	 * @param calendarAcc
	 * @param eventID
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 */
	public void editEvent(String calendarAcc, int eventID, String title,
			String description, String startTime, String endTime) {
		ContentValues cv = new ContentValues();
		if (calendarAcc != null && startTime != null && endTime != null
				&& tZone != null) {
			cv.put("calendar_id", calendarAcc);// Required
			cv.put("title", title);// Optional
			cv.put("description", description);// Optional
			cv.put("dtstart", startTime); // Required
			cv.put("dtend", endTime); // Required
			cv.put("eventTimezone", tZone.getID());// Required for ICS
			
			int rowsUpdated = myResolver.update(
					Uri.parse("content://com.android.calendar/events/"+eventID), cv, null, null);
			if (rowsUpdated == 1) {
				eventCreationFailed = false;
			}
		} else {
			eventCreationFailed = true;
		}
	}
	
	/**
	 * @author Adam
	 * Edits an event in the event table and sends data on to attendees if required.
	 * @param calendarAcc
	 * @param eventID
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 */
	public void updateMethod(String calendarAcc, int eventID, String title,
			String description, String startTime, String endTime, String email) {
		editEvent(calendarAcc, eventID, title,
				description, startTime, endTime);
		HashMap<String, String> attendees = getAttendees(ctx, eventID);
		attendees.remove(email);
		if (attendees != null) {
		boolean isUpdate = true;
		sendToAttendees(calendarAcc, title, description, startTime, endTime, new ArrayList<String>(attendees.keySet()), 0, 0, 0, isUpdate);
		syncMan = new SyncManager();
		syncMan.forceSync(ctx);
		}
	}

	/**
	 * @author Adam
	 * Determines whether attendees are present in the event request
	 * @param attendees
	 * @return
	 */
	private boolean attendeesPresent(ArrayList<String> attendees) {
		if (attendees != null && attendees.size() > 0) {
			hasAttendees = true;
			return hasAttendees;
		} else {
			hasAttendees = false;
			return hasAttendees;
		}
	}

	/**
	 * @author Adam
	 * Sets an alarm and pending intent for an event
	 * @param eventID
	 * @param startTime
	 */
	public void setAlarm(int eventID, String startTime) {
		// Create an alarm for the event
		manager = new ActivityPendingManager(ctx);
		manager.sendPendingIntent(eventID, Long.parseLong(startTime), underTest);
		calObserver = new CalendarObserver(new Handler(), manager, ctx, eventID);
		calObserver.setObserver(Uri
				.parse("content://com.android.calendar/events/" + eventID));
	}
	
	/**
	 * @author Ka Lan
	 * Updates an existing alarm and pending intent for an event
	 * @param eventID
	 * @param startTime
	 */
	public void setUpdateAlarm(int eventID, String startTime) {
		manager = new ActivityPendingManager(ctx);
		manager.editPendingIntent(eventID, Long.parseLong(startTime), underTest);
		calObserver = new CalendarObserver(new Handler(), manager, ctx, eventID);
		calObserver.setObserver(Uri
				.parse("content://com.android.calendar/events/" + eventID));
	}

	/**
	 * @author Adam
	 * Checks to see if all stages of event creation have been successful (i.e. 
	 * inserting into event and attendee tables and returning URIs)
	 */
	public void checkForSuccess() {
		if (newEvent != null && (hasAttendees && newAttendees != null)) {
			successfulInsert = true;
		} else if (newEvent != null && (!hasAttendees && newAttendees == null)) {
			successfulInsert = true;
		} else {
			successfulInsert = false;
		}
	}

	/**
	 * @author Adam
	 * Sends event details to attendees as a JSONObject
	 * @param calendarAcc
	 * @param title
	 * @param description
	 * @param startTime
	 * @param endTime
	 * @param attendees
	 * @param radius
	 * @param lat
	 * @param lon
	 * @param isUpdate
	 */
	private void sendToAttendees(String calendarAcc, String title,
			String description, String startTime, String endTime,
			ArrayList<String> attendees, int radius, int lat, int lon, boolean isUpdate) {
		// package event into a JSON Object
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap.put("calendar_id", calendarAcc);// Required
		eventMap.put("title", title);// Optional
		eventMap.put("description", description);// Optional
		eventMap.put("dtstart", startTime); // Required
		eventMap.put("dtend", endTime); // Required
		eventMap.put("eventTimezone", tZone.getID());// Required for ICS
		eventMap.put("isUpdate", Boolean.toString(isUpdate));
		JSONObject JSONEvent = new JSONObject(eventMap);

		HashMap<String, Integer> boundaryMap = new HashMap<String, Integer>();
		boundaryMap.put("radius", radius);
		boundaryMap.put("latitude", lat);
		boundaryMap.put("longitude", lon);
		JSONObject JSONBoundary = new JSONObject(boundaryMap);

		HashMap<String, String> attendeeMap = new HashMap<String, String>();
		for (int i = 0; i < attendees.size(); i++) {
			attendeeMap.put("attendee" + i, attendees.get(i));
		}
		JSONObject JSONAttendee = new JSONObject(attendeeMap);

		JSONObject JSONCombined = new JSONObject();
		try {
			JSONCombined.put("eventObject", JSONEvent);
			JSONCombined.put("attendeeObject", JSONAttendee);
			JSONCombined.put("boundaryObject", JSONBoundary);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// send to our server to pass on via GCM
		sendEvent(JSONCombined, attendees);
	}

	/**
	 * @author Adam
	 * Adds the eventID and boundary data to a local SQLite database
	 * @param eventID 
	 * @param radius in metres (0 if no boundary set)
	 * @param lat latitude in microseconds (0 if no boundary set)
	 * @param lon longitude in microseconds (0 if no boundary set)
	 */
	public void addToDB(int eventID, int radius, int lat, int lon) {
		if (eventID != -1 && successfulInsert) {
			database.open();
			database.insertEvent(eventID, radius, lat, lon);
			database.close();
		}
	}

	/**
	 * @author Adam This method queries the calendar to return all events,
	 *         orders them in descending numerical order by event ID and selects
	 *         the first item (i.e. highest id number).
	 */
	public int findLastEventID(String calendar) {
		if (!eventCreationFailed) {
			cursor = myResolver.query(
					Uri.parse("content://com.android.calendar/events"),
					new String[] { "_id" }, "calendar_id = " + calendar, null,
					"_id DESC");
			// note: previously used "ownerAccount = " + gmail as filter.
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int highestID = cursor.getInt(0);
				cursor.close();
				return highestID;
			} else {
				cursor.close();
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * @author Ka Lan This method checks if the eventId is valid.
	 */
	public boolean validEventId(int eventId) {
		cursor = myResolver.query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "_id", "title", "dtstart", "dtend",
						"description", "deleted" }, "_id = " + eventId, null,
				null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor.getInt(5) == 1) {
				cursor.close();
				return false;
			} else {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	/**
	 * This method retrieves the details from a given event and returns them as
	 * a HashMap.
	 * 
	 * @param eventID
	 */
	public HashMap<String, String> getEventDetails(int eventID) {
		HashMap<String, String> eventDetails = new HashMap<String, String>();
		// To get selected details for last event and display them
		Cursor cursor = myResolver.query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "_id", "title", "dtstart", "dtend",
						"description" }, "_id = " + eventID, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			eventDetails.put("title", cursor.getString(1));
			eventDetails.put("dtstart", cursor.getString(2));
			eventDetails.put("dtend", cursor.getString(3));
			eventDetails.put("description", cursor.getString(4));
		} else {
			eventDetails = null;
		}
		cursor.close();
		return eventDetails;
	}
	
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, Long[]> getEventTimes(ArrayList<Integer> eventIds) {
		HashMap<Integer, Long[]> results = new HashMap<Integer, Long[]>();
		for (int eventId: eventIds) {
			Cursor cursor = myResolver.query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "_id",  "dtstart", "dtend"}, "_id = " + eventId, null, null);
			if (cursor.getCount() > 0) {
				Long[] eventIdArray = new Long[2];
				cursor.moveToFirst();
				eventIdArray[0] = Long.parseLong(cursor.getString(1));
				eventIdArray[1] = Long.parseLong(cursor.getString(2));
				results.put(eventId, eventIdArray);
				cursor.close();
			}
		}
		return results;
	}
	/**
	 * This method retrieves the end time from a given event and returns it as
	 * a HashMap.
	 * 
	 * @param eventID
	 */
	public HashMap<String, String> getEndTime(int eventID) {
		HashMap<String, String> endTime = new HashMap<String, String>();
		// To get selected details for last event and display them
		Cursor cursor = myResolver.query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "dtend" }, "_id = " + eventID, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			endTime.put("dtend", cursor.getString(0));
		} else {
			endTime = null;
		}
		cursor.close();
		return endTime;
	}

	/**
	 * @author Ka Lan
	 * Returns a string of the details for an event
	 * @param eventIds
	 * @return
	 */
	public ArrayList<String> getEventDetailsList(ArrayList<Integer> eventIds) {
		ArrayList<String> eventDetails = new ArrayList<String>();
		Iterator<Integer> itr = eventIds.iterator();
		while (itr.hasNext()) {
			StringBuilder sb = new StringBuilder();
			Cursor cursor = myResolver.query(
					Uri.parse("content://com.android.calendar/events"),
					new String[] { "_id", "title", "dtstart", "dtend" },
					"_id = " + itr.next(), null, null);
			cursor.moveToFirst();
			sb.append(" Title: ");
			sb.append(cursor.getString(1));
			sb.append("\n");
			sb.append(" Start Date/Time: ");
			sb.append(TimeConverter.convertEpochTimeToDateTime(Long
					.parseLong(cursor.getString(2))));
			sb.append("\n");
			sb.append(" End Date/Time: ");
			sb.append(TimeConverter.convertEpochTimeToDateTime((Long
					.parseLong(cursor.getString(3)))));
			eventDetails.add(sb.toString());
			cursor.close();
		}

		return eventDetails;
	}

	/**
	 * This method retrieves attendees for a given event and returns them as a
	 * HashMap with gmail addresses as keys and user's names as the values.
	 * 
	 * @param ctx
	 *            a context for getting the ContentResolver.
	 * @param eventID
	 *            ID of the event to query.
	 */
	public static HashMap<String, String> getAttendees(Context ctx, int eventID) {
		HashMap<String, String> attendees = new HashMap<String, String>();
		Cursor cursor = ctx.getContentResolver().query(
				Uri.parse("content://com.android.calendar/attendees"),
				new String[] { "event_id", "attendeeEmail", "attendeeName" },
				"event_id = " + eventID, null, null); // and attendee status =
														// 1?
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				attendees.put(cursor.getString(1), cursor.getString(2));
				cursor.moveToNext();
			}
		} else {
			return null;
		}
		cursor.close();
		return attendees;
	}

	/**
	 * @author Adam
	 * Creates a thread (if required) to send event data to other users via 
	 * the GCM service.
	 * @param eventObject
	 * @param users
	 */
	public void sendEvent(JSONObject eventObject, ArrayList<String> users) {
		if (sendEventHandler == null) {
			sendEventHandler = new Handler();
			sendEventThread = new SendEventThread(sendEventHandler,
					eventObject, users);
			sendEventThread.start();
			return;
		}

		if (sendEventThread.getState() != Thread.State.TERMINATED) {
			// Thread is already there
		} else {
			// Create new thread
			sendEventThread = new SendEventThread(sendEventHandler,
					eventObject, users);
			sendEventThread.start();
		}

	}

	/**
	 * @author Adam
	 * This method is used by the GCMIntent service to identify an existing event in the Android
	 * calendar after a forced sync. Once the event has been found the eventID is returned.
	 * @param dtstart
	 * @param dtend
	 * @param title
	 * @return
	 */
	public String compareEvents(String dtstart, String dtend, String title) {
		String eventID = null;
		Log.i("CompareEvents input", dtstart + " " + dtend + " " + title);
		Cursor cursor = myResolver.query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "_id", "title", "dtstart", "dtend" }, "title='"
						+ title + "' AND dtstart='" + dtstart + "' AND dtend='"
						+ dtend + "'", null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 1) {
			eventID = cursor.getString(0);
		}// Also want to capture if there is no matching events or more than one
			// event
			// e.g. do a more thorough comparison
		setEventCreationFailed(false);
		int tempEventID = findLastEventID("2");
		HashMap<String, String> details = getEventDetails(tempEventID);
		Collection<String> values = details.values();
		for (String string : values) {
			Log.i("CompareEvents HashMap", string);
		}
		return eventID;
	}

	public void deleteEvent(Uri eventUri) {
		myResolver.delete(eventUri, null, null);
	}

	// Getters and Setters

	/**
	 * @return the ctx
	 */
	public Context getCtx() {
		return ctx;
	}

	/**
	 * @return the eventTitle
	 */
	public String getEventTitle() {
		return eventTitle;
	}

	/**
	 * @param eventTitle
	 *            the eventTitle to set
	 */
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	/**
	 * @return the eventStart
	 */
	public String getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart
	 *            the eventStart to set
	 */
	public void setEventStart(String eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public String getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd
	 *            the eventEnd to set
	 */
	public void setEventEnd(String eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return eventDescription;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.eventDescription = description;
	}

	/**
	 * @return the tZone
	 */
	public TimeZone gettZone() {
		return tZone;
	}

	/**
	 * @param tZone
	 *            the tZone to set
	 */
	public void settZone(TimeZone tZone) {
		this.tZone = tZone;
	}

	/**
	 * @return the cursor
	 */
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * @return the myResolver
	 */
	public ContentResolver getMyResolver() {
		return myResolver;
	}

	/**
	 * @return the successfulInsert
	 */
	public boolean getSuccessfulInsert() {
		return successfulInsert;
	}

	/**
	 * @return the newEvent
	 */
	public Uri getNewEvent() {
		return newEvent;
	}

	/**
	 * @param newEvent
	 *            the newEvent to set
	 */
	public void setNewEvent(Uri newEvent) {
		this.newEvent = newEvent;
	}

	/**
	 * @return the newAttendees
	 */
	public Uri getNewAttendees() {
		return newAttendees;
	}

	/**
	 * @param newAttendees
	 *            the newAttendees to set
	 */
	public void setNewAttendees(Uri newAttendees) {
		this.newAttendees = newAttendees;
	}

	public boolean getEventCreationFailed() {
		return eventCreationFailed;
	}

	public void setEventCreationFailed(boolean eventCreationFailed) {
		this.eventCreationFailed = eventCreationFailed;
	}

	public void setOwnerEvent(boolean ownerEvent) {
	}

	public CalendarObserver getCalObserver() {
		return calObserver;
	}

	public boolean isUnderTest() {
		return underTest;
	}

	public void setUnderTest(boolean underTest) {
		this.underTest = underTest;
	}

	public ActivityPendingManager getManager() {
		return manager;
	}

	public void setSuccessfulInsert(boolean successfulInsert) {
		this.successfulInsert = successfulInsert;
	}
	
	public SharedPreferences getPrefs() {
		return prefs;
	}

}
