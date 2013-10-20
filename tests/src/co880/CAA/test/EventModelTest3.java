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
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import junit.framework.TestCase;

public class EventModelTest3 extends ActivityInstrumentationTestCase2<LocationActivity> {
	
	private LocationActivity LocActSetUp;
	private EventModel eventModel;
	private CalendarModel myCalendar;
	private SharedPreferences pref;
	private String calID;
	private ArrayList<String> users;

	public EventModelTest3() {
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

	/**
	 * A test to check that an event is correctly identified
	 * as being active and then expired. 
	 */
	
	/*
	public void testCheckExpiredEvent() {
		myCalendar.insertNewEvent("2", "testCheckExpiredEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()), Long.toString(System.currentTimeMillis()+10000), 
				null, 0, 0, 0);//Starts a new intent straight away
		assertTrue(myCalendar.getSuccessfulInsert());
		eventModel.setEventID(myCalendar.findLastEventID("2"));
		eventModel.checkExpiryTime();
		assertTrue(eventModel.isEventActive());
		try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eventModel.checkExpiryTime();
		assertFalse(eventModel.isEventActive());
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}
	*/
}
