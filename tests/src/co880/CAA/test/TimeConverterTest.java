package co880.CAA.test;

import android.test.AndroidTestCase;
import co880.CAA.Model.TimeConverter;

public class TimeConverterTest extends AndroidTestCase {

	TimeConverter tc;
	
	public TimeConverterTest() {
		super();
		tc = new TimeConverter();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testParseDateAndTime() {
		String epoch = tc.parseDateAndTime("02/03/1988 12:35");
		assertEquals("570890100000", epoch);
	}
	
	public void testConvertEpochToTimeFormat() {
		String time = tc.convertEpochToTimeFormat(570890100000L);
		assertEquals("12:35", time);
	}
	
	public void testDateAlmalgamation() {
		String date = tc.dateAmalgamation(2, 3, 1988);
		assertEquals("2/3/1988", date);
	}
	
	public void testTimeAlmalgamation() {
		String time = tc.timeAmalgamation("15", "27");
		assertEquals("15:27", time);
	}

}
