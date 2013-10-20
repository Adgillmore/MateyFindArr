package co880.CAA.test;

import co880.CAA.Activities.EditEvent;
import android.test.ActivityInstrumentationTestCase2;

public class EditEventTest extends ActivityInstrumentationTestCase2<EditEvent> {

	private EditEvent editEvent;
	
	public EditEventTest() {
		super("co880.CAA.Activities", EditEvent.class);
	}
	
	protected void setUp() throws Exception {
		editEvent = getActivity();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSetDateAndTimeTest() {
		editEvent.setDateAndTime("11/21/2012 09:08:00", "start");
		assertTrue(editEvent.getmMonth() == 11);
		editEvent.setDateAndTime("11/22/2012 09:08:00", "end");
		assertTrue(editEvent.geteDay() == 22);
	}

}
