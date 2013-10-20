package co880.CAA.test;

import java.util.ArrayList;

import co880.CAA.Activities.CreateEvent;
import co880.CAA.Model.CalendarModel;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

public class CalendarModelTest extends ActivityInstrumentationTestCase2<CreateEvent> {

	private Activity eventActivity;
	private CalendarModel myCalendar;
	private Cursor testCursor;
	private ArrayList<String> users;
	
	public CalendarModelTest() {
		super(CreateEvent.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		eventActivity = getActivity();
		myCalendar = new CalendarModel(eventActivity);
		users = new ArrayList<String>();
		users.add("user1@gmail.com");
		users.add("user2@gmail.com");
		myCalendar.setOwnerEvent(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCalendarModel() {
		assertNotNull(myCalendar.getCtx());
		assertNotNull(myCalendar.getMyResolver());
		assertNotNull(myCalendar.gettZone());
		
		assertNull(myCalendar.getEventStart());
		assertNull(myCalendar.getEventEnd());
		assertNull(myCalendar.getEventTitle());
		assertNull(myCalendar.getDescription());
		assertNull(myCalendar.getCursor());
		
		assertFalse(myCalendar.getSuccessfulInsert());
	}


	public void testInsertNewEvent1() {
		//Event with attendees
		//Note, if you test a time in the past then the second test hangs as the LocationActivity intent is lauched
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), 
				users, 0, 0, 0);
		assertTrue(myCalendar.getSuccessfulInsert());
	}
	
	public void testInsertNewEvent2() {
		//Event without attendees
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), null, 0, 0, 0);
		assertTrue(myCalendar.getSuccessfulInsert());
	}
	
	public void testInsertBadEvent1() {
		//Event with Calendar missing
		myCalendar.insertNewEvent(null, "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), users, 0, 0, 0);
		assertFalse(myCalendar.getSuccessfulInsert());
	}
	
	public void testInsertBadEvent2() {
		//Event with start time missing
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				null, Long.toString(System.currentTimeMillis()+7200000), users, 0, 0, 0);
		assertFalse(myCalendar.getSuccessfulInsert());
	}
	
	public void testInsertBadEvent3() {
		//Event with end time missing
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), null, users, 0, 0, 0);
		assertFalse(myCalendar.getSuccessfulInsert());
	}
	
	public void testInsertBadEvent4() {
		//Event with timezone missing
		myCalendar.settZone(null);
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000), users, 0, 0, 0);
		assertFalse(myCalendar.getSuccessfulInsert());
	}
	
	public void testFindLastEventID() {
		testCursor = myCalendar.getMyResolver().query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "_id"}, "calendar_id = 2", null, null);
		myCalendar.setEventCreationFailed(false);
		int lastEvent = myCalendar.findLastEventID("2");
		boolean lastEventIsMax = false;
		
		testCursor.moveToFirst();
		for(int i=0; i<testCursor.getCount(); i++) {
			int tempEvent = testCursor.getInt(0);
			if (lastEvent >= tempEvent) { //lastEvent will be equal to itself
				lastEventIsMax = true;
				testCursor.moveToNext();
			} else {
				lastEventIsMax = false;
			}
		}
		assertTrue(lastEventIsMax);
	}
	
	public void testShortEvent() {
		//Insert short event to test app startup and end
		myCalendar.insertNewEvent("2", "testEvent", "An event to test system behaviour", 
				Long.toString(System.currentTimeMillis()+30000), Long.toString(System.currentTimeMillis()+90000), 
				users, 0, 0, 0);
		assertTrue(myCalendar.getSuccessfulInsert());
	}
	

}
