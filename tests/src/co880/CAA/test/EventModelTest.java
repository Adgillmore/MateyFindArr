package co880.CAA.test;

import java.util.ArrayList;
import java.util.HashMap;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.AlarmIntent.ActivityPendingManager;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.EventModel;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import junit.framework.TestCase;

public class EventModelTest extends ActivityInstrumentationTestCase2<LocationActivity> {
	
	private LocationActivity LocActSetUp;
	private EventModel eventModel;
	private CalendarModel myCalendar;
	private ArrayList<String> users;
	private SharedPreferences pref;
	private String calID;

	public EventModelTest() {
		super("co880.CAA.Activities", LocationActivity.class);
	}
	
	/**
	 * Creates an instance of the main menu (CAAActivity) and
	 * a separate instance of CalendarModel since we want to 
	 * destroy CAAActivity and open a new instance to test its
	 * state.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		LocActSetUp = getActivity();
		myCalendar = new CalendarModel(LocActSetUp);
		pref = LocActSetUp.getSharedPreferences("caaPref", Activity.MODE_WORLD_READABLE);
		calID = pref.getString("calendarId", null);
		users = new ArrayList<String>();
		users.add("user1@gmail.com");
		users.add("user2@gmail.com");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEventModel() {
		eventModel = LocActSetUp.getCurrentEvent();
		assertSame(LocActSetUp, eventModel.getActivity());
		assertNotNull(pref);
		assertNotNull(eventModel.getMyCalendar());
		assertFalse(eventModel.hasBoundary());
	}
	
	
	public void testGetEndTime() {
		long endTime = System.currentTimeMillis() + 120000;
		myCalendar.setUnderTest(true);
		myCalendar
				.insertNewEvent(calID, "testCheckSession1",
						"An event to test system behaviour",
						Long.toString(System.currentTimeMillis()),
						Long.toString(endTime),
						users, 0, 0, 0);
		eventModel = LocActSetUp.getCurrentEvent();
		eventModel.getMyCalendar().setEventCreationFailed(false);
		int eventID = myCalendar.findLastEventID(calID);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("eventID", eventID);
		editor.commit();
		
		LocActSetUp.finish();

		LocationActivity LocActTestObject = openApp();
	
		eventModel.getMyCalendar().setEventCreationFailed(false);
		eventModel.setEventID(eventModel.getMyCalendar().findLastEventID(calID));
		HashMap<String, String> endTimeMap = new HashMap<String, String>();
		endTimeMap.put("dtend", Long.toString(endTime));
		eventModel.setEndTime(endTimeMap);

		assertEquals(endTime, eventModel.getEndTime());
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}

	/**
	 * A test to check that the attendees are correctly
	 * retrieved using the eventID.
	 */

	public void testGetAttendeeList() {
		myCalendar.insertNewEvent(calID, "testGetAttendeeList", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), 
				users, 0, 0, 0);
		LocActSetUp.finish();
		
		LocationActivity LocActTestObject = openApp();
		eventModel.setEventID(myCalendar.findLastEventID(calID));
		HashMap<String, String> gotAttendees = eventModel.getAttendeeList();
		assertTrue(gotAttendees.containsKey("user1@gmail.com"));
		assertTrue(gotAttendees.containsKey("user2@gmail.com"));
		assertTrue(gotAttendees.size() == 2);
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}

	/**
	 * A method to simulate opening the app. This also sets
	 * the eventModel using the field in the new instance of
	 * (CAAActivity).
	 * @return An instance of the main menu activity (CAAActivity)
	 */
	private LocationActivity openApp() {
		LocationActivity LocAct = getActivity();
		eventModel = LocAct.getCurrentEvent();//This always returns eventID as -1 even when set in shared prefs
		return LocAct;
	}
	
}
