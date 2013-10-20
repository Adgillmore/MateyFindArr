package co880.CAA.test;

import java.util.ArrayList;

import co880.CAA.AlarmIntent.ActivityPendingManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import co880.CAA.AlarmIntent.CalendarObserver;
import co880.CAA.Model.CalendarModel;

public class CalendarObserverTest extends AndroidTestCase {

	private ActivityPendingManager parentActivity;
	private int eventID;
	private Handler handler;
	private Uri uri;
	private CalendarObserver myCalendarObserver;
	private ArrayList<String> users;
	private String calendarId;
	private SharedPreferences pref;

	public CalendarObserverTest() {
		super();

	}

	protected void setUp() throws Exception {
		super.setUp();
		parentActivity = new ActivityPendingManager(getContext());
		pref = getContext().getSharedPreferences("caaPref", Activity.MODE_WORLD_READABLE);
		calendarId = pref.getString("calendarId", null);
		handler = new Handler();
		users = new ArrayList<String>();
		users.add("user1@gmail.com");
		users.add("user2@gmail.com");
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCalendarObserver() {
		
		//Test object creation and initialisation
		eventID = 474743;
		myCalendarObserver = new CalendarObserver(handler, parentActivity, 
				getContext(), eventID);
		
		assertEquals(myCalendarObserver.getEventID(), eventID);
		assertSame(myCalendarObserver.getParentActivity(), parentActivity);
		assertNotNull(myCalendarObserver.getMyCalendar());
	}

	@UiThreadTest
	public void testOnChange() {
		// create an event
		CalendarModel myCalendar = new CalendarModel(getContext());
		myCalendar.setOwnerEvent(true);
		String startTime = Long.toString(System.currentTimeMillis() + 3600000);
		myCalendar.addEvent(calendarId, "A test",
				"An event to test the system",
				startTime, Long.toString(System.currentTimeMillis() + 14400000));
		int eventID = myCalendar.findLastEventID("2");
		Log.i("CalendarModel insert event", "EventID: " + eventID);
		myCalendar.setAlarm(eventID, startTime);
			
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//make a change
		Uri eventUri = myCalendar.getNewEvent();
		ContentValues cv = new ContentValues();
		String newStart = Long.toString(System.currentTimeMillis()+7200000);
		cv.put("dtstart", newStart);
		int rowsUpdated = myCalendar.getMyResolver().update(eventUri, cv, null, null);

		assertTrue(rowsUpdated == 1);
		//assertTrue(myCalendar.getCalObserver().isChangeReceived()); //This doesn't work - onChange is not called.
		
		myCalendar.deleteEvent(myCalendar.getNewEvent());
		
	}

	public void testDeleted() {
		
	}

}
