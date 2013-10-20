package co880.CAA.test;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Model.RetrieveGoogleCalendarIdNo;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;


public class RetrieveGoogleCalendarIdNoTest extends
		ActivityInstrumentationTestCase2<CAAActivity> {
	
	private CAAActivity caAct;
	private RetrieveGoogleCalendarIdNo retGoog;
	private SharedPreferences pref;
 

	public RetrieveGoogleCalendarIdNoTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}

	protected void setUp() throws Exception {
		caAct = getActivity();
		pref = caAct.getSharedPreferences("caaPref",  1);
		retGoog = new RetrieveGoogleCalendarIdNo(caAct);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetGoogleAccount() {
		String temp = retGoog.getGoogleAccount(pref.getString("email", null));
		assertNotNull(temp);
	}
	
	public void testWriteCalendarAcc() {
		String tempEmail = pref.getString("email", null);
		if (tempEmail != null) {
			retGoog.writeCalendarAcc(tempEmail);
			assertNotNull(pref.getString("calendarId", null));
		}
		
	}
	

}
