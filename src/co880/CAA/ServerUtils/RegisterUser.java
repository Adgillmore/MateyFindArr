package co880.CAA.ServerUtils;

import java.util.regex.Pattern;

import com.google.android.gcm.GCMRegistrar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Toast;

/**
 * 
 * @author Dan
 * This class searches a users phone to find their email address, validates it 
 * as a gmail address and sends it to the server to register them after the 
 * user has given consent.
 *
 */
public class RegisterUser {

	private Context c;
	private SharedPreferences r;
	private SharedPreferences gcmPref;
	private SendRegistration upload;
	private String email;
	boolean check;

	public RegisterUser(Context c) {
		this.c = c;
		r = c.getSharedPreferences("caaPref", Context.MODE_WORLD_READABLE);
		check = false;
		gcmPref = c.getSharedPreferences("com.google.android.gcm",
				Context.MODE_PRIVATE);
	}

	/**
	 * Creates and account manager and generates a list of
	 * email addresses and calls test to validate them.
	 * @return
	 */
	public String getEmail() {
		Account[] accounts = AccountManager.get(c).getAccounts();
		for (Account account : accounts) {
			String possibleEmail = testEmail(account.name);
			if (possibleEmail != null) {
				email = possibleEmail;
				return email;
			}
		}
		return null;
	}

	/**
	 * Tests an email to see if it is has a valid gmail address
	 * format
	 * @param email
	 * @return an email address with valid gmail format
	 */
	public String testEmail(String email) {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		String newEmail = null;
		if (emailPattern.matcher(email).matches()) {
			String possibleEmail = email;
			if (possibleEmail.contains("@gmail")
					|| possibleEmail.contains("@googlemail")) {
				newEmail = possibleEmail;
			}
		}
		return newEmail;
	}

	/**
	 * Calls the AsyncTask to send the gmail address and GCM
	 * registrationID to the server
	 * @param context
	 */
	public void register(Context context) {
		upload = new SendRegistration(context);
		String newEmail = r.getString("email", null);
		String gcmUserId = gcmPref.getString("regId", "");
		String[] params = { newEmail, gcmUserId };
		if (newEmail != null) {
			upload.execute(params);
		} else {
			Toast.makeText(context, "Email is at register() method",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean getClose() {
		return check;
	}

	public SendRegistration getUpload() {
		return upload;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
