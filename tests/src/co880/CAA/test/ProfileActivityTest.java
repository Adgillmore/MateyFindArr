package co880.CAA.test;

import co880.CAA.Activities.ProfileActivity;
import co880.CAA.ServerUtils.RegisterUser;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

public class ProfileActivityTest extends ActivityInstrumentationTestCase2<ProfileActivity> {
	

	/**
	 * @author KaLan
	 * Simulates onClick test to see if the sharedPref email has been removed when logged out.
	 */
	private ProfileActivity mActivity;
	private RegisterUser regUser;
	private SharedPreferences pref;

	public ProfileActivityTest() {
		super("co880.CAA.Activities", ProfileActivity.class);
	}



	protected void setUp() throws Exception {
		mActivity = getActivity();
		pref = mActivity.getSharedPreferences("caaPref", mActivity.MODE_WORLD_READABLE);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testLogoutClick() {
		TouchUtils.clickView(this, mActivity.getLogOut());
		String nullEmail = mActivity.getPref().getString("email", null);
		assertEquals(null, (nullEmail));
		
		//Log back in so subsequent tests don't fail
		regUser = new RegisterUser(mActivity);
		String emailAddress = regUser.getEmail();
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("email", emailAddress);
		editor.commit();
	}
	
}
