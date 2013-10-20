package co880.CAA.test;

import java.util.ArrayList;

import co880.CAA.Activities.MyEvents;
import co880.CAA.Model.MyEventDb;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

public class MyEventsTest extends ActivityInstrumentationTestCase2<MyEvents> {
	
	private MyEvents myEvent;
	private SharedPreferences pref;
	private String calendarId;
	private MyEventDb eventDb;
	
	public MyEventsTest() {
		super("co880.CAA.MyEvents", MyEvents.class);
	}

	protected void setUp() throws Exception {
		myEvent = getActivity();
		myEvent.getDatabase().open();
		pref = myEvent.getSharedPreferences("caaPref", Activity.MODE_WORLD_READABLE);
		calendarId = pref.getString("calendarId", null);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		myEvent.getDatabase().close();
		super.tearDown();
	}
	

	public void testGetEventsIds() {
		myEvent.getDatabase().insertEvent(118118, 42, 42, 42);
		ArrayList<Integer> eventIds = eventDb.getEventsIds();
		assertTrue(eventIds.contains(118118));
		myEvent.getDatabase().deleteEvent(118118);
	}
	
	public void testGetAllEventsdetails() {
		ArrayList<Integer> entry = new ArrayList<Integer>();
		myEvent.getMyCalendar().addEvent(calendarId, "Bobby's dance recital", "An epic rendition of Shakespeare's Tempest in the artform of dancing.", Long.toString(System.currentTimeMillis()+3600000), Long.toString(System.currentTimeMillis()+7200000));
		int eventId = myEvent.getMyCalendar().findLastEventID(calendarId);
		entry.add(eventId);
		ArrayList<String> details = myEvent.getAllEventsDetails(entry);
		assertTrue(!details.isEmpty());
		myEvent.getMyCalendar().deleteEvent(Uri
				.parse("content://com.android.calendar/events/" + eventId));
	}

}
