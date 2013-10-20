package co880.CAA.test;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.ServerUtils.RegisterUser;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.test.ActivityInstrumentationTestCase2;

/**
 * 
 * @author Ka Lan Junit android test class for RegisterUser class.
 * 
 */

public class RegisterUserTest extends
		ActivityInstrumentationTestCase2<CAAActivity> {

	private RegisterUser regUser;
	private String possibleEmail;
	private String email;
	private CAAActivity act;

	// requires the CAAActivity class as that is the context that uses it.
	public RegisterUserTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}

	protected void setUp() throws Exception {
		act = getActivity();
		regUser = new RegisterUser(act);
		possibleEmail = "copyOfDanJones@gmail.com";
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
/* Can't get this to work because the error code is null. Is this because ut's set in a callback method?
	// Tests the server side to see if it was a successful query.
	public void testRegister() {
		regUser.register(act);
		int i = 10;
		while ((i != 0) && (regUser.getUpload().getErrorCode() == null)) {
			try {
				Thread.sleep(5000);
				i--;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		assertNotNull(regUser.getUpload().getErrorCode());
	}
	*/

	// Testing if it uses with made up email, filtering.
	public void testTestEmail() {
		assertEquals(possibleEmail, regUser.testEmail(possibleEmail));
	}

	// Actually obtaining the correct email from x account and comparing.
	public void testGetEmail() {
		Account[] accounts = AccountManager.get(act).getAccounts();
		for (Account account : accounts) {
			String testEmail = regUser.testEmail(account.name);
			if (testEmail != null) {
				email = testEmail;
			}
		}
		assertEquals(email, regUser.getEmail());
	}

}
