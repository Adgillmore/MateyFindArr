package co880.CAA.test;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Model.EventIDChecker;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class EventIDCheckerTest extends ActivityInstrumentationTestCase2<CAAActivity> {

	EventIDChecker ec;
	CAAActivity a;
	
	public EventIDCheckerTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		ec = new EventIDChecker();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SuppressWarnings("static-access")
	public void testGetEventID() {
		Intent intent = new Intent();
		intent.putExtra("fake", 666);
		ec.setIntent(intent);
		assertEquals(-1, ec.getEventID(a, false));
	}

	@SuppressWarnings("static-access")
	public void testGetEventID2() {
		Intent intent = new Intent();
		intent.putExtra("eventID", 666);
		ec.setIntent(intent);
		assertEquals(666, ec.getEventID(a, true));
	}
}
