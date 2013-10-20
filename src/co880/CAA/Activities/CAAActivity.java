package co880.CAA.Activities;

import com.google.android.gcm.GCMRegistrar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import co880.CAA.Model.EventIDChecker;
import co880.CAA.Model.EventManager;
import co880.CAA.Model.RetrieveGoogleCalendarIdNo;
import co880.CAA.R;
import co880.CAA.ServerUtils.RegisterHandler;
import co880.CAA.ServerUtils.RegisterThread;
import co880.CAA.ServerUtils.RegisterUser;

/**
 * 
 * @author Dan - Start the application, set up the menu and retrieve user's
 *         gmail/googlemail address in order send to other activities i.e.
 *         LocationActivity 
 *         Additional contributors: Adam & Ka lan.
 */

public class CAAActivity extends Activity {

	String email;
	private SharedPreferences pref;
	private RetrieveGoogleCalendarIdNo retrGoog;
	// private int StartTime;
	RegisterUser ru;
	Button ce;
	Button me;
	Button cre;
	Button debug;
	String test;
	private EventManager eventMan;
	private Handler registerHandler;
	private RegisterThread registerThread;
	public static final String TAG = "CAAActivity";

	/** Called when the activity is first created. */
	@SuppressLint("WorldReadableFiles")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Molot.otf");
        TextView tv = (TextView) findViewById(R.id.title);
        tv.setTypeface(tf);
		
		// Set up Buttons from Dashboard
		ce = (Button) findViewById(R.id.button1);
		me = (Button) findViewById(R.id.button2);
		cre = (Button) findViewById(R.id.button3);
		//debug = (Button) findViewById(R.id.button4);
		ru = new RegisterUser(this);
		retrGoog = new RetrieveGoogleCalendarIdNo(this);
		pref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);

		// Check if an event is active
		int eventID = EventIDChecker.getEventID(this, false);
		eventMan = new EventManager(this, eventID);
		eventMan.setEventID(eventID);
		eventMan.checkExpiryTime();
		setButtons(false);
		
		if (!GCMRegistrar.isRegistered(this)) {
			registerWithGCM();
		}

		// registerWithGCM();

		// Create thread and progress dialog for registration
		// if GCMRegistrar.isRegistered true then and progress and setButtons
		/*
		 * if (!GCMRegistrar.isRegistered(this)) { setButtons(); }
		 */

		// Set the onClickListener for Current Event button
		ce.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("co880.CAA",
						"co880.CAA.Activities.LocationActivity"));
				startActivity(intent);
			}
		});

		// Set the onClickListener for My Events button
		me.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("co880.CAA",
						"co880.CAA.Activities.MyEvents"));
				startActivity(intent);
			}
		});

		// Set the onClickListener for Create Event button
		cre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("co880.CAA",
						"co880.CAA.Activities.CreateEvent"));
				startActivity(intent);
			}
		});
		
		/*
		// Set the onClickListener for Debug button
				debug.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						//Whatever you want to debug
						MyEventDb eventDb = new MyEventDb(ctx);
						eventDb.openRead();
						CalendarModel model = new CalendarModel(ctx);
						model.setEventCreationFailed(false);
						int eventID = model.findLastEventID("2");
						Cursor cursor = eventDb.queryEvent(eventID);
						cursor.moveToFirst();
						if (cursor.getInt(2) >0) {
							int boundaryCentreLat = cursor.getInt(0);
							int boundaryCentreLon = cursor.getInt(1);
							int boundaryRadius = cursor.getInt(2);
							
						} else {
							
						}
						cursor.close();
						eventDb.close();
					}
					
				});
				*/
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		finish();
		startActivity(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		eventMan.checkExpiryTime(EventIDChecker.getEventID(this, false));

		// Search for a saved email address in SharedPreferences
		
		test = pref.getString("email", null);
		findEmail(test);

		// retrGoog.writeCalendarAcc(pref.getString("email", null));
		// Toast.makeText(getBaseContext(), "Acc No. " +
		// pref.getString("calendarId", null), Toast.LENGTH_LONG).show();

		// whatsMyEmail(c);

		setButtons(GCMRegistrar.isRegistered(this));

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		eventMan.checkExpiryTime(EventIDChecker.getEventID(this, false));
		if (!eventMan.isEventActive()) {
			ce.setEnabled(false);
		} else {
			ce.setEnabled(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		eventMan.checkExpiryTime(EventIDChecker.getEventID(this, false));
		if (!eventMan.isEventActive()) {
			ce.setEnabled(false);
		} else {
			ce.setEnabled(true);
		}
	}

	public void registerWithGCM() {

		if (registerHandler == null) {
			registerHandler = new RegisterHandler(this);
			registerThread = new RegisterThread(registerHandler, this);
			registerThread.start();
			return;
		}

		if (registerThread.getState() != Thread.State.TERMINATED) {
			// Thread is already there
		} else {
			// Create new thread
			registerThread = new RegisterThread(registerHandler, this);
			registerThread.start();
		}

	}

	/**
	 * @author Adam & Ka lan
	 * Method to disable and enable buttons accordingly to registration.
	 * @param isRegistered
	 */
	public void setButtons(boolean isRegistered) {
		if (pref.getString("email", null) != null) {
			if (eventMan.isEventActive()) {
				ce.setEnabled(isRegistered);
			} else {
				ce.setEnabled(false);
			}
			me.setEnabled(isRegistered);
			cre.setEnabled(isRegistered);
		}
		else {
			ce.setEnabled(false);
			me.setEnabled(false);
			cre.setEnabled(false);
		}
	}

	/**
	 * @author Dan - Creates an alert dialog which prompts user to agree to use email address
	 * or to reject
	 */
	public void createAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		final String emailAddress = ru.getEmail();

		// set title - user's email address which will be used as identifier
		alertDialogBuilder.setTitle(emailAddress);

		// set dialog message
		alertDialogBuilder
				.setMessage("Register with this email address?")
				.setCancelable(false)
				.setPositiveButton("Yarrrr!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SharedPreferences.Editor editor = pref.edit();
								editor.putString("email", emailAddress);
								editor.commit();
								//@author Adam & Ka lan implemented logic to conincide with GCM registration.
								if (GCMRegistrar
										.isRegistered(getApplicationContext())) {
									ru.register(getApplicationContext());
									retrGoog.writeCalendarAcc(pref.getString(
											"email", null));
									setButtons(GCMRegistrar
											.isRegistered(getApplicationContext()));
									dialog.cancel();
								} else {
									dialog.cancel();
									Toast.makeText(getApplicationContext(),
											"Try Again", Toast.LENGTH_SHORT)
											.show();
									createAlert();
								}
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		if (emailAddress != null) {
			alertDialog.show();
		} else {
			Log.i(TAG, "Create Dialog: No google account found on this device.");
			// this shouldn't be possible.
		}
	}

	public void whatsMyEmail(String s) {
		Toast.makeText(this,
				s + ": " + pref.getString("email", "Nothing - gutted"),
				Toast.LENGTH_SHORT).show();
	}

	public void findEmail(String test) {
		// If no saved email search for an email to use and create an alert for
		// the user to confirm
		if (test == null) {
			createAlert();

			// Otherwise inform user of email address being used
		} else {
			//Toast.makeText(this, "Using " + test + " as Email address",
					//Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * @author Dan - create the options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(pref.getString("email", null) == null) {	
			MenuItem fmMenu = menu.findItem(R.id.friend);
			MenuItem ceMenu = menu.findItem(R.id.create);
			MenuItem meMenu = menu.findItem(R.id.MyE);
			
			fmMenu.setEnabled(false);
			ceMenu.setEnabled(false);
			meMenu.setEnabled(false);
		}
		
		return super.onPrepareOptionsMenu(menu);	
	}

	public SharedPreferences getpref() {
		return pref;
	}

	/**
	 * @author Dan - Method for options menu, which creates a new intent based on menu item
	 * pressed and diverts user to relevant activity
	 * @param MenuItem - selected menu item
	 * @return Boolean true - intent dealt with
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Settings:
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.PrefActivity"));
			startActivity(intent);
			break;
		case R.id.friend:
			Intent intent1 = new Intent();
			intent1.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.FriendManagerActivity"));
			startActivity(intent1);
			break;
		case R.id.create:
			Intent intent2 = new Intent();
			intent2.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.CreateEvent"));
			startActivity(intent2);
			break;
		case R.id.MyE:
			Intent intent3 = new Intent();
			intent3.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.MyEvents"));
			startActivity(intent3);
			break;
		case R.id.Dash:
			Toast.makeText(this, "Dashboard!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.Prof:
			Intent intent5 = new Intent();
			intent5.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.ProfileActivity"));
			startActivity(intent5);
			break;
		}
		return true;
	}

	public RegisterUser getRu() {
		return ru;
	}

	public EventManager getCurrentEvent() {
		return eventMan;
	}

	public Button getCe() {
		return ce;
	}

	public Button getMe() {
		return me;
	}

	public Button getCre() {
		return cre;
	}

	public Handler getRegisterHandler() {
		return registerHandler;
	}

	public RegisterThread getRegisterThread() {
		return registerThread;
	}

}