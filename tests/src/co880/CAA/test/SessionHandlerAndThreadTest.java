package co880.CAA.test;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import co880.CAA.Activities.CAAActivity;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.EventIDChecker;
import co880.CAA.Model.EventManager;
import co880.CAA.Model.EventModel;
import co880.CAA.Model.LocationService;
import junit.framework.TestCase;

public class SessionHandlerAndThreadTest extends ActivityInstrumentationTestCase2<LocationActivity> {
	private LocationActivity LocActSetUp;
	private EventModel eventModel;
	private CalendarModel myCalendar;
	private SharedPreferences pref;
	private String calID;
	private ArrayList<String> users;
	private LocationService locService;

	public SessionHandlerAndThreadTest() {
		super("co880.CAA.Activities", LocationActivity.class);
	}
	
	
	/**
	 * Creates an instance of the main menu (CAAActivity) and 
	 * initialises CalendarModel and EventModel using the fields
	 * belonging to CAAActivity. Dialog boxes triggered by the
	 * pendingIntent are disabled with 'underTest = true'
	 */
	protected void setUp() throws Exception {
		super.setUp();
		locService = new LocationService();
		LocActSetUp = getActivity();
		pref = LocActSetUp.getSharedPreferences("caaPref", Activity.MODE_WORLD_READABLE);
		calID = pref.getString("calendarId", null);
		eventModel = new EventModel(LocActSetUp, EventIDChecker.getEventID(LocActSetUp, false));
		
		users = new ArrayList<String>();
		users.add("user1@gmail.com");
		users.add("user2@gmail.com");
		


	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	/**
	 * A test to check SessionHandler initiation
	 */
	public void testSessionHandlerState() {
		//Show that handler has not been initialised yet.
		assertNull(eventModel.getSessionHandler());
		
		//Initialise the handler
		locService.setEventModel(eventModel);
		
		//Check that the handler has been initialised
		assertNotNull(eventModel.getSessionHandler());
	}

	
	/**
	 * A test to check Thread initiation
	 */
	public void testSessionThreadState() {
		//Show that thread has not been initialised yet.
		assertNull(eventModel.getSessionThread());

		//Initialise the handler
		locService.setEventModel(eventModel);
		
		//Create an active event
		myCalendar = eventModel.getMyCalendar();
		myCalendar.setUnderTest(true); //switches off dialog boxes
		String end = Long.toString(System.currentTimeMillis()+360000);
		myCalendar.addEvent(calID, "testSessionThreadState", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()), end);
		
		//Set up event model fields
		int eventID = myCalendar.findLastEventID(calID);
		eventModel.setEventID(eventID);
		HashMap<String, String> endTime = new HashMap<String, String>();
		endTime.put("dtend", end);
		eventModel.setEndTime(endTime);

		eventModel.runSessionThread(locService);
		assertTrue(eventModel.getSessionThread().getState() != Thread.State.TERMINATED);
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}
	
	/**
	 * A test to check Thread timer correctly identifies
	 * event expiry.
	 */
	/* THIS TEST HANGS WHEN IT GETS TO THE THREAD SLEEP
	 * 
	public void testThreadTimer() {
		// Show that thread has not been initialised yet.
		assertNull(eventModel.getSessionThread());

		// Initialise the handler
		locService.setEventModel(eventModel);

		// Create an active event
		myCalendar = eventModel.getMyCalendar();
		myCalendar.setUnderTest(true); // switches off dialog boxes
		String end = Long.toString(System.currentTimeMillis() + 20000);
		myCalendar.addEvent(calID, "testThreadTimer",
				"An event to test system behaviour",
				Long.toString(System.currentTimeMillis()), end);

		// Set up event model fields
		int eventID = myCalendar.findLastEventID(calID);
		eventModel.setEventID(eventID);
		HashMap<String, String> endTime = new HashMap<String, String>();
		endTime.put("dtend", end);
		eventModel.setEndTime(endTime);
		
		//eventModel.setEventActive(true);
		eventModel.runSessionThread(locService);
		SharedPreferences updatePref = PreferenceManager
				.getDefaultSharedPreferences(LocActSetUp);
		long updateCycle = Long.valueOf(updatePref.getString("listPref", "0"));
		
		
		try {
			Thread.sleep(Long.valueOf(updatePref.getString("listPref", "0")+10000));//Needs to be longer than one 'sleep' cycle of the timer thread
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		State threadState = eventModel.getSessionThread().getState();
		//Check handler received message
		assertTrue(eventModel.getSessionHandler().hasMessages(13));
		assertTrue(eventModel.getSessionThread().getState() == Thread.State.TERMINATED);
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	}
	*/

	/**
	 * A test to check that the message passed from the handler
	 * terminates the session.
	 */
	/*
	//DOESN'T WORK
	public void testHandlerMessage() {
		myCalendar.addEvent(calID, "testSessionThreadState", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()), Long.toString(System.currentTimeMillis()+20000), 
				null);
		int eventID = myCalendar.findLastEventID(calID);
		myCalendar.checkForSuccess();
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("eventID", eventID);
		editor.commit();
		eventModel.setUpEvent(caaActSetUp, eventID);
		eventModel.setEventActive(true);
		eventModel.runSessionThread();
		caaActSetUp.setButtons(true);
		assertTrue(caaActSetUp.getCe().isEnabled());
		
		try {
			Thread.sleep(70000);//Needs to be longer than one 'sleep' cycle of the timer thread
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		State threadState = eventModel.getSessionThread().getState();
		
		//Need to somehow wait while the main UI thread processes the message and then do
		//the following two tests.
		assertFalse(eventModel.isEventActive());
		assertFalse(caaActSetUp.getCe().isEnabled());
				
		myCalendar.deleteEvent(myCalendar.getNewEvent());
	} //DOESN'T WORK
	
	*/
	
	
}

