package co880.CAA.Model;


import com.google.android.gcm.GCMRegistrar;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.ServerUtils.DeleteLocationData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * @author Adam
 *This class checks the expiry time of an event and manages starting
 *and stopping of the session through controlled access to LocationActivity.
 */
public class EventManager extends ExpiryChecker{

	private SharedPreferences pref;
	private CAAActivity activity;
	private Handler sessionHandler;
	private SessionThread sessionThread;
	private AlertDialog startAlert;
	private AlertDialog endAlert;
	private boolean underTest;
	private boolean noDataOnServer;
	private Intent serviceIntent;
	private static final String TAG = "EventManager";

	@SuppressLint("WorldReadableFiles")
	public EventManager(CAAActivity inActivity, int eventID) {
		super(inActivity, eventID);
		activity = inActivity;
		pref = activity.getSharedPreferences("caaPref",
				Activity.MODE_WORLD_READABLE);
		buildStartAlert();
		buildEndAlert();
		serviceIntent = new Intent(activity,
				LocationService.class);
	}

	/**
	 * @author Adam
	 * Checks the expiry time of the event and runs the open or close methods as required
	 */
	public void checkExpiryTime() {
		// Check if event has expired

		if (getEndTime() > System.currentTimeMillis()) {// event running
			Log.i(TAG, "EventID: " + getEventID() + "active");
			openSession(activity);
		} else { // event expired or doesn't exist
			Log.i(TAG, "No event active");
			closeSession(activity);
			if ((System.currentTimeMillis() - getEndTime()) < 60000L
					&& !underTest) {
				// Event recently expired
				Log.i(TAG, "Event Expired");
				endAlert.show();
			}
		}
	}
	
	/**
	 * @author Adam
	 * An alternative expiry checker for use with a specified eventID to enforce 
	 * early exiting of the sharing session (artificial expiry). 
	 * @param specificEventID
	 */
	public void checkExpiryTime(int specificEventID) {
		// Force a check of the manually modified event to show that it has expired
		setEndTime(getMyCalendar().getEndTime(specificEventID));
		if (getEndTime() > System.currentTimeMillis()) {// event running
			Log.i(TAG, "EventID: " + getEventID() + "active");
			openSession(activity);
		} else { // event expired or doesn't exist
			Log.i(TAG, "No event active");
			closeSession(activity);
			if ((System.currentTimeMillis() - getEndTime()) < 60000L
					&& !underTest) {
				// Event recently expired
				Log.i(TAG, "Event Expired");
				endAlert.show();
			}
		}
	}
	
	/**
	 * @author Adam
	 * Toggles the server data 'dirty bit' and shows the
	 * launch dialog if required.
	 * @param activity The activity to retrieve the 'extra' from 
	 */
	public void openSession(Activity activity) {
		setEventActive(true);
		SharedPreferences.Editor editor = pref.edit();
		Log.i(TAG, "Started logging location data");
		editor.putBoolean("noDataOnServer", false);
		editor.commit();
		Intent thisIntent = activity.getIntent();
		boolean newLaunch = thisIntent.getBooleanExtra("newLaunch", false);
		if (newLaunch && !underTest) {
			startAlert.show();
			thisIntent.removeExtra("newLaunch");
		}
	}
	
	/**
	 * @author Adam
	 * Toggles the server data 'dirty bit', 
	 * deletes location data, 
	 * removes the eventID from preferences,
	 * stops the LocationService
	 * and shows the end dialog if required.
	 * @param activity The activity to be closed
	 */
	public void closeSession(Activity activity) {
		setEventActive(false);
		noDataOnServer = pref.getBoolean("noDataOnServer", true);
		if (!noDataOnServer) {
			DeleteLocationData delete = new DeleteLocationData(activity);
			delete.execute(pref.getString("email", null));
			Log.i(TAG, "Deleted location data for EventID: " + super.getEventID());
		}
		
		activity.stopService(serviceIntent);

		if (super.getEventID() != -1) {
			Log.i("EventModel event expired", "EventID: " + super.getEventID());
			// remove old events from preferences
			SharedPreferences.Editor editor = pref.edit();
			editor.remove("eventID");
			editor.commit();
			Log.i("EventModel removed from prefs", "EventID: " + super.getEventID());
		}
	}

	/**
	 * @author Adam
	 * Prepares the Dialog for launching a location sharing session
	 */
	public void buildStartAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Session Started")
				.setMessage("Would you like to start sharing your location?")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (sessionHasNotExpired()) {
							Intent intent = new Intent();
							intent.setComponent(new ComponentName("co880.CAA",
									"co880.CAA.Activities.LocationActivity"));
							activity.startActivity(intent);
						} else {
							setEventActive(false);
							(activity)
									.setButtons(GCMRegistrar
											.isRegistered(activity));
						}
					}
				})
				.setNeutralButton("Not now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (!sessionHasNotExpired()) {
									setEventActive(false);
									(activity).setButtons(GCMRegistrar
											.isRegistered(activity));
								}
								dialog.cancel();
							}
						})
				.setNegativeButton("Never",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								pref.edit().remove("eventID").commit();
								setEventActive(false);
								(activity).setButtons(GCMRegistrar
											.isRegistered(activity));
								dialog.cancel();
							}
						});
		setStartAlert(builder.create());
	}

	/**
	 * @author Adam
	 * Prepares the Dialog shown after a sharing session has ended
	 */
	private void buildEndAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Session Expired")
				.setMessage("Location sharing disabled.").setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DeleteLocationData delete = new DeleteLocationData(
								activity);
						delete.execute(pref.getString("email", null));
						dialog.cancel();
					}
				});
		endAlert = builder.create();
	}
	
	public boolean sessionHasNotExpired() {
		if ((getEndTime() > System.currentTimeMillis())) {
			return true;
		}
		return false;
	}

	public AlertDialog getStartAlert() {
		return startAlert;
	}

	public void setStartAlert(AlertDialog startAlert) {
		this.startAlert = startAlert;
	}

	public AlertDialog getEndAlert() {
		return endAlert;
	}

	public Handler getSessionHandler() {
		return sessionHandler;
	}

	public SessionThread getSessionThread() {
		return sessionThread;
	}

	public boolean getNoDataOnServer() {
		return noDataOnServer;
	}

	public void setNoDataOnServer(boolean noDataOnServer) {
		this.noDataOnServer = noDataOnServer;
	}

	public CAAActivity getActivity() {
		return activity;
	}

}
