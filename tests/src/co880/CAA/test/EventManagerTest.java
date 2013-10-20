package co880.CAA.test;

import java.util.ArrayList;
import co880.CAA.Activities.CAAActivity;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.EventManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

public class EventManagerTest extends ActivityInstrumentationTestCase2<CAAActivity> {
	
	private CAAActivity caaActSetUp;
	private EventManager eventManager;
	private CalendarModel myCalendar;
	private ArrayList<String> users;
	private SharedPreferences pref;
	private String calID;

	public EventManagerTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}
	
	/**
	 * Creates an instance of the main menu (CAAActivity) and
	 * a separate instance of CalendarModel since we want to 
	 * destroy CAAActivity and open a new instance to test its
	 * state.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		caaActSetUp = getActivity();
		myCalendar = new CalendarModel(caaActSetUp);
		myCalendar.setOwnerEvent(true);
		pref = caaActSetUp.getSharedPreferences("caaPref", Activity.MODE_WORLD_READABLE);
		calID = pref.getString("calendarId", null);
		users = new ArrayList<String>();
		users.add("user1@gmail.com");
		users.add("user2@gmail.com");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testEventModel() {
		eventManager = caaActSetUp.getCurrentEvent();
		assertSame(caaActSetUp, eventManager.getActivity());
		assertNotNull(pref);
		assertNotNull(eventManager.getMyCalendar());
		assertNotNull(eventManager.getStartAlert());
		assertNotNull(eventManager.getEndAlert());
	}
	/*
	update time display
	
	get end time

	*/
	public void testGetEndTime() {
		long endTime = System.currentTimeMillis() + 120000;
		myCalendar
				.insertNewEvent(calID, "testCheckSession1",
						"An event to test system behaviour",
						Long.toString(System.currentTimeMillis() + 60000),
						Long.toString(endTime),
						users, 0, 0, 0);
		caaActSetUp.finish();

		CAAActivity caaActTestObject = openApp();
		eventManager.getMyCalendar().setEventCreationFailed(false);
		eventManager.setEventID(eventManager.getMyCalendar().findLastEventID(calID));
		//eventManager.getEvent(caaActTestObject, eventManager.getEventID());
		assertEquals(endTime, eventManager.getEndTime());
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}

	/**
	 * A test to check that an event is correctly identified
	 * as not being active yet.
	 */
	public void testCheckSession1() {
		// No event active
		myCalendar
				.insertNewEvent(calID, "testCheckSession1",
						"An event to test system behaviour",
						Long.toString(System.currentTimeMillis() + 60000),
						Long.toString(System.currentTimeMillis() + 120000),
						users, 0, 0, 0);
		caaActSetUp.finish();

		CAAActivity caaActTestObject = openApp();
		assertEquals(-1, eventManager.getEventID());
		assertEquals(-1, pref.getInt("eventID", -1));
		assertEquals(-1, eventManager.getActivity().getIntent()
				.getIntExtra("eventID", -1));
		assertFalse(caaActTestObject.getCe().isEnabled());

		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}

	/**
	 * A test to check that the expiry time has not passed
	 */
	public void testCheckExpiryTime() {
		myCalendar.insertNewEvent(calID, "testCheckExpiryTime", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+240000), Long.toString(System.currentTimeMillis()+360000), 
				null, 0, 0, 0);
		caaActSetUp.finish();
		
		CAAActivity caaActTestObject = openApp();
		eventManager.setEventID(myCalendar.findLastEventID(calID));
		eventManager.checkExpiryTime();
		assertTrue(myCalendar.getSuccessfulInsert());
		assertTrue(eventManager.isEventActive());
		assertFalse(caaActTestObject.getCe().isEnabled());
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}

	/**
	 * A method to simulate opening the app. This also sets
	 * the eventModel using the field in the new instance of
	 * (CAAActivity).
	 * @return An instance of the main menu activity (CAAActivity)
	 */
	private CAAActivity openApp() {
		CAAActivity caaAct = getActivity();
		eventManager = caaAct.getCurrentEvent();
		return caaAct;
	}
	
}
