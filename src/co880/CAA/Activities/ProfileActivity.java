package co880.CAA.Activities;

import co880.CAA.R;
import co880.CAA.ServerUtils.RegisterUser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Dan - This class displays a user's profile details such as email
 *         address used for the app &etc...
 * 
 */

public class ProfileActivity extends Activity {

	private TextView t;
	private String email;
	private SharedPreferences pref;
	private Button logOut;
	private Button logIn;
	private SharedPreferences.Editor editor;
	private RegisterUser ru;
	private boolean loggedIn;

	@SuppressLint("WorldReadableFiles")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Molot.otf");
        TextView tv = (TextView) findViewById(R.id.profileTitle);
        tv.setTypeface(tf);

		// Retrieve users email address from SharedPreferences and set TextView
		// to this
		t = (TextView) findViewById(R.id.emailAddress);
		pref = getSharedPreferences("caaPref", Context.MODE_WORLD_READABLE);
		email = pref.getString("email", "Not Logged In");
		if (!email.equals("Not Logged In")) {
			loggedIn = true;
		}
		t.setText(email);

		editor = pref.edit();

		ru = new RegisterUser(this);

		logOut = (Button) findViewById(R.id.logout);
		logOut.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				editor.clear();
				editor.commit();
				Toast.makeText(getBaseContext(), "Logged Out",
						Toast.LENGTH_LONG).show();
				t.setText("Not Logged In");
				loggedIn = false;
				logButtons();
			}
		});

		logIn = (Button) findViewById(R.id.login);
		logIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				createAlert();
			}
		});
		
		logButtons();
	}
	
	
	public Button getLogOut() {
		return logOut;
	}
	
	public SharedPreferences getPref() {
		return pref;
	}

	/**
	 * @author Dan - create an alert into which user can enter email address
	 * in order to log in
	 */
	public void createAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// set title - user's email address which will be used as identifier
		alert.setTitle("Please enter an email address");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setMessage("Log in with a gmail address.")
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String posEmail = ru.testEmail(input.getText().toString());
								if (input.getText() != null && posEmail != null) {
									Toast.makeText(getBaseContext(), "Logging In",
											Toast.LENGTH_SHORT).show();
									t.setText(input.getText().toString().toLowerCase());
									editor.putString("email", input.getText().toString());
									editor.commit();
									ru.register(getBaseContext());
									loggedIn = true;
									logButtons();
									dialog.cancel();
								} else {
									dialog.cancel();
									createErrorAlert();
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alert.create();

		// show it
		alertDialog.show();
	}

	/**
	 * @author Dan - create alert to inform user of invalid email addresses
	 */
	public void createErrorAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		// set title - user's email address which will be used as identifier
		alert.setTitle("Invalid Email Address")
				.setMessage(
						"Please enter a valid gmail or googlemail email address")
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alert.create();

		// show it
		alertDialog.show();
	}
	
	/**
	 * @author Ka lan
	 * Implemented logic to disable and enable button accordingly.
	 */
	public void logButtons() {
		if (loggedIn == false) {
			logIn.setEnabled(true);
			logOut.setEnabled(loggedIn);
		}
		else {
			logIn.setEnabled(false);
			logOut.setEnabled(true);
		}
	}
}