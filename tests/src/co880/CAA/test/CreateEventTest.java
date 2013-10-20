package co880.CAA.test;

import java.util.Calendar;
import co880.CAA.Activities.CreateEvent;
import co880.CAA.Model.CalendarModel;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

public class CreateEventTest extends ActivityInstrumentationTestCase2<CreateEvent> {
	
	private CreateEvent createEvent;
	private Calendar cal;

	public CreateEventTest() {
		super("co880.CAA.Activities", CreateEvent.class);
	}

	protected void setUp() throws Exception {
		createEvent = getActivity();
		createEvent.getDatabase().open();
		cal = Calendar.getInstance();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

	}
	
	public void testSetDateAndTimeTest() {
		createEvent.getDateAndTime();
		assertTrue(createEvent.getmMonth() == cal.get(Calendar.MONTH));
		assertTrue(createEvent.geteDay() == cal.get(Calendar.DAY_OF_MONTH));
	}
	
	public void testOnCreateDialog() {
		assertNotNull(createEvent.onCreateDialog(0));
		assertNotNull(createEvent.onCreateDialog(1));
		assertNotNull(createEvent.onCreateDialog(2));
		assertNotNull(createEvent.onCreateDialog(3));
		assertNull(createEvent.onCreateDialog(4));
	}
	
	public void testPad() {
		assertEquals("55", CreateEvent.pad(55));
		assertEquals("04", CreateEvent.pad(4));
	}
	
	public void testCompareTimes() {
		assertTrue(createEvent.compareTimes(1000000000000L, 1100000000000L, 1300000000000L, 1500000000000L));
		assertTrue(createEvent.compareTimes(1500000000000L, 1600000000000L, 1000000000000L, 1100000000000L));
		assertFalse(createEvent.compareTimes(1300000000000L, 1600000000000L, 1100000000000L, 1400000000000L));
		assertFalse(createEvent.compareTimes(1000000000000L, 1500000000000L, 1100000000000L, 1300000000000L));
	}
	
	public void testValidNewEventTime() {
		CalendarModel cm = new CalendarModel(createEvent);
		cm.insertNewEvent(createEvent.getCalendarId(), "TestTestTest", "Testing", Long.toString(1000000000000L), Long.toString(1100000000000L), null, 0, 0, 0);
		int eventId = cm.findLastEventID(createEvent.getCalendarId());
		assertFalse(createEvent.validNewEventTime(Long.toString(1010000000000L), Long.toString(1300000000000L)));
		assertFalse(createEvent.validNewEventTime(Long.toString(1000000000000L), Long.toString(1100000000000L)));
		createEvent.getDatabase().deleteEvent(eventId);
		cm.deleteEvent(Uri
				.parse("content://com.android.calendar/events/"
						+ eventId));
	
		}

	
	}
