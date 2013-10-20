package co880.CAA.test;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.ServerUtils.RegisterUser;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

/**
 * 
 * @author KaLan
 *Junit android test for CAAActivity class, checks if the logic works for the findEmail method.
 */
public class CAAActivityTest extends
		ActivityInstrumentationTestCase2<CAAActivity> {

	private CAAActivity caaAct;
	private SharedPreferences pref;
	private RegisterUser regUser;

	public CAAActivityTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}

	protected void setUp() throws Exception {
		caaAct = getActivity();
		pref = caaAct.getpref();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	
		
	}

	//This tests if the email is null will it add one into shared preferences.
	public void testFindEmailNull() {
		//Deregister the user
		pref.edit().remove("email").commit();
		//Reregister
		regUser = caaAct.getRu();
		String emailAddress = regUser.getEmail();
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("email", emailAddress);
		editor.commit();
		//Check registration was successful
		assertTrue(pref.getString("email", null) != null);
	}
	
	

}
