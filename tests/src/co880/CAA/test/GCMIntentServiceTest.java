package co880.CAA.test;

import java.net.PasswordAuthentication;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.CreateEvent;
import co880.CAA.Model.CalendarModel;
import co880.CAA.ServerUtils.GetUsersLocHandler;
import junit.framework.TestCase;

public class GCMIntentServiceTest extends ActivityInstrumentationTestCase2<CreateEvent> {
	
	private Activity eventActivity;
	private CalendarModel myCalendar;
	private Cursor testCursor;
	private ArrayList<String> users;
	
	public GCMIntentServiceTest() {
		super(CreateEvent.class);		
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		eventActivity = getActivity();
		myCalendar = new CalendarModel(eventActivity);
		users = new ArrayList<String>();
		//users.add("adgillmore@gmail.com");
		//users.add("mimicdanjay@googlemail.com");
		//users.add("kalanleung@googlemail.com");
		myCalendar.setOwnerEvent(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSetNearEvent() {
		//This fake test is a convenient way to set an event in the calendar in a minute's time 
		//and lasts three minutes
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+60000), Long.toString(System.currentTimeMillis()+180000), 
				users, 0, 0, 0);
		assertTrue("Send remote event", true);
		
		//myCalendar.deleteEvent(myCalendar.getNewEvent());

	}
	
	/*
	public void testSetFarEvent() {
		//This fake test is a convenient way to set an event in the calendar in 
 		//in an hour's time and lasts an hour.
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), 
				users, null);
		assertTrue("Send remote event", true);
		
		//myCalendar.deleteEvent(myCalendar.getNewEvent());

	}
	*/

}
