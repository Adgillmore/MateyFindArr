package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import co880.CAA.ServerUtils.GetUsersLocHandler;
import co880.CAA.ServerUtils.GetUsersLocThread;
import co880.CAA.Model.BoundaryOverlay;
import co880.CAA.Model.EventIDChecker;
import co880.CAA.Model.EventModel;
import co880.CAA.Model.LocationService;
import co880.CAA.Model.MyEventDb;
import co880.CAA.Model.LocationService.LocalBinder;
import co880.CAA.Model.MyItemizedOverlay;
import co880.CAA.Model.OtherItemizedOverlay;
import co880.CAA.Model.Utils;
import co880.CAA.LocationServiceBroadcastReceiver;
import co880.CAA.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Primary: Dan. Additional: Adam & Ka
 * This Activity is responsible for combining the all location sharing aspects
 * into a UI environment
 */

public class LocationActivity extends MapActivity {
	private MapView mapView;
	private MapController controller;
	private Drawable drawableDefault;
	private Drawable drawableActive;
	private Drawable drawableInactive;
	private Drawable drawableMe;
	private List<Overlay> mapOverlays;
	private MyItemizedOverlay myItemizedOverlay;
	private OtherItemizedOverlay otherItemizedOverlay;
	private BoundaryOverlay boundaryOverlay;
	private OverlayItem overlayItem;
	private TextView activeUsersTextView;
	private TextView timeRemaining;

	private Location location;
	private GeoPoint myGeoPoint;

	private Handler getLocationsHandler;
	public GetUsersLocThread getLocationsThread;
	private String[][] otherUsersLocations;
	private String[] keys;
	private Set<String> users;
	private HashMap<String, String> attendees;
	private SharedPreferences pref;
	private String email;
	private EventModel currentEvent;
	LocationService mService;
	boolean mBound = false;
	boolean myReceiverRegistered = false;
	private LocationServiceBroadcastReceiver myReceiver;
	private float boundaryRadius;
	private GeoPoint boundaryCentre;
	private LocationActivity locationActivity;
	private MyEventDb database;
	private ArrayList<String> activeUserList;
	private int activeUserCount;

	// Intent filter for use in LocationServiceBroadcastReciever
	final IntentFilter theFilter = new IntentFilter("co880.CAA.NEW_LOCATION");

	/** Called when the activity is first created. */
	@SuppressLint("WorldReadableFiles")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// If there is no LocationSrevice running, start one
		if (!isMyLocationServiceRunning()) {
				Intent intentService = new Intent(this, LocationService.class);
				startService(intentService);
			}
		
		//Set up local database
		database = new MyEventDb(this);
		database.open();
		
		// Set up sharing event
		int eventID = EventIDChecker.getEventID(this, false);
		currentEvent = new EventModel(this, eventID);
		currentEvent.setEventID(eventID);
		currentEvent.checkExpiryTime();
		currentEvent.checkBoundary();

		//Initialise locationActivity field for use in embedded classes
		locationActivity = this;
		
		// Create LocationServiceBroadcastReciever in order to receive
		// broadcasts about new locations
		myReceiver = new LocationServiceBroadcastReceiver();
		myReceiver.setActivity(this);

		// Retrieve users email if previously saved
		pref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
		email = pref.getString("email",
				"Error at LocationActivity pref.getString");

		// Set boolean to confirm that data now on server - used in order to
		// delete
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("noDataOnServer", false);
		editor.commit();
		Log.i("LocationActivity setDeletedData", "false");

		// Find attendees from EventModel
		attendees = currentEvent.getAttendeeList();
		if (attendees != null) {
			users = attendees.keySet();
			users.remove(email);
		} else {
			users = null;
		}

		setupGuiElements();

		// Initialise thread handling

		// Instantiate Handlers and worker threads
		getLocationsHandler = null;
		getLocationsThread = null;

		//List of data to get from the server
		keys = new String[6];
		keys[0] = "gmail";
		keys[1] = "latitude";
		keys[2] = "longitude";
		keys[3] = "speed";
		keys[4] = "latestTimestamp";
		keys[5] = "status";
	}

	@Override
	public void onPause() {
		super.onPause();
		// when activity pauses, stop listening for location updates.
		if (getLocationsThread != null) {
			// stop getting other users' locations
			getLocationsThread.setQueryServer(false);
		}

		// Stop listening for broadcasts regarding own location
		if (myReceiverRegistered) {
			unregisterReceiver(myReceiver);
			myReceiverRegistered = false;
		}	
		database.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Start listening for broadcasts regarding own location
		if (!myReceiverRegistered) {
			this.registerReceiver(myReceiver, theFilter);
			myReceiverRegistered = true;
		}
		
		// enable retrieval of other users' locations
		if (getLocationsThread != null) {
			getUsersLocations();
		}
		database.open();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocationService
		Intent intent = new Intent(this, LocationService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Stop getting other users' locations
		if (getLocationsThread != null) {
			getLocationsThread.setQueryServer(false);
		}

		// Unbind from service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		mapOverlays.remove(boundaryOverlay);
	}
	
	@Override
	protected void onRestart() {
		super.onStart();
		// Enable retrieval of other users' locations
		if (getLocationsThread != null) {
			getUsersLocations();
		}
	}
	
	public void setupGuiElements() {
		setContentView(R.layout.location);

		// Extract mapView from layout & zooming function.
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);

		// Instantiate MapController.
		controller = mapView.getController();
		controller.setZoom(15);

		// Create overlays for user and friends.
		mapOverlays = mapView.getOverlays();
		drawableMe = this.getResources().getDrawable(R.drawable.marker);
		drawableDefault = this.getResources().getDrawable(R.drawable.marker_amber);
		drawableActive = this.getResources().getDrawable(R.drawable.marker_green);
		drawableInactive = this.getResources().getDrawable(R.drawable.marker_red);
		
		myItemizedOverlay = new MyItemizedOverlay(drawableMe, this);
		otherItemizedOverlay = new OtherItemizedOverlay(drawableDefault, drawableActive,
				drawableInactive, this);
		
		// Simple button to re-centre map to users location.
		Button follow = (Button) findViewById(R.id.center_location);
		follow.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (location != null) {
					controller.animateTo(Utils.locationToGeopoint(location));
				}
			}
		});

		// Infobar at bottom of screen
		activeUsersTextView = (TextView) findViewById(R.id.activeUsers);
		timeRemaining = (TextView) findViewById(R.id.time_remaining);
		createUsersDialog();
		activeUserCount = 0;
		activeUsers();
	}

	/**
	 * @author Ka Lan
	 * this method checks if our LocationService is already running.
	 * @return true if running, false if not.
	 * Used StackOverflow as an reference. 
	 */
	public boolean isMyLocationServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	    	Log.e("Services running", "" + service.service.getClassName());
	    	if ("co880.CAA.Model.LocationService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	/**
	 * @author Dan - Find the service and bind to it, also send EventModel to said
	 *         service
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// Bind to LocalService, cast the IBinder and get LocalService
			// instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mService.setEventModel(currentEvent);
			mService.initiateBoundary(boundaryCentre, boundaryRadius);
			if (mService.getEventModel().hasBoundary()) {
				boundaryOverlay = new BoundaryOverlay(locationActivity,
						boundaryCentre.getLatitudeE6(), boundaryCentre
						.getLongitudeE6(), boundaryRadius);
				if (!mapOverlays.contains(boundaryOverlay)) {
				mapOverlays.add(boundaryOverlay);
				}
			}
			if (mService.getLocation() != null) {
				Location location = mService.getLocation();
				drawMyLocation(location);
				controller.animateTo(Utils.locationToGeopoint(location));
				mService.processMyLocation(location);
			}	
			mBound = true;
		}
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.location_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.exit:
			database.deleteEvent(currentEvent.getEventID());
			currentEvent.finishEarly(this);
			break;
		case R.id.Dash2:
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.CAAActivity"));
			startActivity(intent);
			this.finish();
			break;
		}
		return true;
	}

	/**
	 * @author Dan - Draw a users location.
	 * @param loc
	 *            Location to draw
	 */
	public void drawMyLocation(Location loc) {
		Integer speed;
		if (loc.hasSpeed()) {
			speed = Math.round(loc.getSpeed());
		} else {
			speed = 0;
		}
		String speedString = speed.toString();
		String timeString = Utils.convertEpochToTimeFormat(loc.getTime());
		location = loc;
		myGeoPoint = Utils.locationToGeopoint(location);
		overlayItem = new OverlayItem(myGeoPoint, email, "Speed: "
				+ speedString + " mph \n Time: " + timeString);
		myItemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(myItemizedOverlay);
	}

	/**
	 * @author Adam - Creates Handler (if required) and thread, and retrieves
	 *         other users' locations. Runs as a loop until queryServer is set
	 *         to false.
	 * @param users
	 *            - A String[] of users identified by gmail addresses.
	 * @param keys
	 *            - The information to fetch from the server.
	 */
	public void getUsersLocations() {
		if (users != null) {
			if (getLocationsHandler == null) {
				getLocationsHandler = new GetUsersLocHandler(this, keys);
				getLocationsThread = new GetUsersLocThread(getLocationsHandler,
						users, this);
				getLocationsThread.start();
				return;
			}

			if (getLocationsThread.getState() != Thread.State.TERMINATED) {
				// Thread is already there
			} else {
				// Create new thread
				getLocationsThread = new GetUsersLocThread(getLocationsHandler,
						users, this);
				getLocationsThread.start();
			}
		}
	}

	/**
	 * @author Dan - Create dialog showing users' currently attending event
	 */
	private void createUsersDialog() {

		activeUsersTextView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Users' that are actively sharing locations
				if (otherUsersLocations != null) {
					AlertDialog alert = buildUsersDialog();
					alert.show();
				} else {
					Toast.makeText(LocationActivity.this,
							"No other attendees", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	/**
	 * @author Dan - Build an AlertDialog consisting of all active users'
	 * @return AlertDialog
	 */
	public AlertDialog buildUsersDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LocationActivity.this);
		activeUserList = new ArrayList<String>();
		builder.setTitle("Current Attendees");
		if (otherUsersLocations != null) {
		for (int i = 0; i < otherUsersLocations.length; i++) {
			String text = otherUsersLocations[i][0];
			if (!text.equals("null")) {
				activeUserList.add(text);
			}
			activeUserCount = activeUserList.size();
		}
		}
		ArrayList<String> tempUsers = new ArrayList<String>(users);
		tempUsers.removeAll(activeUserList);
		for (String string : tempUsers) {
			activeUserList.add(string + " - INACTIVE");
		}

		final CharSequence[] attendeesArray = new CharSequence[activeUserList
				.size()];
		for (int i = 0; i < attendeesArray.length; i++) {
			attendeesArray[i] = activeUserList.get(i);
		}

		builder.setItems(attendeesArray, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
			}
		});

		AlertDialog alert = builder.create();
		return alert;
	}

	

	/**
	 * @author Dan - set the time remaining TextView
	 * @param String
	 *            - time to set time remaining to
	 */
	public void setTimeRemainingText(String time) {
		timeRemaining.setText(time);
	}

	/**
	 * @author Ka Lan - A message that displays speed and distance of friends upon clicking.
	 * @param Geopoint p - In order to measure distance to users' own location
	 * @param String speed - The speed at which the said user is traveling
	 * @param String timestamp - the time at which the said users' location was last taken
	 * @return String message - to display in dialog
	 */
	private String getMessage(GeoPoint p, String speed, String timestamp) {
		StringBuilder strBuild = new StringBuilder();
		int dis = Utils.getDistance(p, location);
		Float speedValue = 2.237f * Float.parseFloat(speed);
		String time = Utils.convertEpochToTimeFormat(Long.parseLong(timestamp));
		String msg2 = "N/A \n";
		String msg = null;

		strBuild.append("Distance: ");
		if (dis != 0) {
			strBuild.append(dis + " m" + "\n");
		} else {
			strBuild.append(msg2);
		}
		strBuild.append("Speed: ");
		if (speedValue != 0) {
			strBuild.append(speed + "MPH \n");
		} else {
			strBuild.append(msg2);
		}
		strBuild.append("Time: ");
		if (time != null) {
			strBuild.append(time + "\n");
		} else {
			strBuild.append(msg2);
		}
		msg = strBuild.toString();
		return msg;
	}

	// Must override for MapView
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	

	/**
	 * @author Adam - Mutator method to store locations retrieved from the
	 *         server in a field. Used by the getLocationsHandler
	 *         handleMessage() callback.
	 * @param locations
	 *            - Locations retrieved from server
	 */
	public void setOtherUsersLocs(String[][] locations) {
		otherUsersLocations = locations;
	}

	/**
	 * @author Adam - draw the overlays for those users' currently using app
	 */
	public void drawOthers() {
		// Create second overlay
		Integer lat = 0;
		Integer lon = 0;
		String speed = "";
		String name = "";
		String timestamp = "";
		Integer status = 0;
		mapView.invalidate();

		if (otherUsersLocations != null) {
			for (int i = 0; i < otherUsersLocations.length; i++) {
				if (otherUsersLocations[i][0].equals("null")) {
					return; // replace with method to search subarray for "null"
				} else {
					lat = Integer.parseInt(otherUsersLocations[i][1]);
					lon = Integer.parseInt(otherUsersLocations[i][2]);
					speed = otherUsersLocations[i][3];
					if (attendees.get(otherUsersLocations[i][0]) != null) {
						name = attendees.get(otherUsersLocations[i][0]);
					} else {
						name = otherUsersLocations[i][0];
					}
					timestamp = otherUsersLocations[i][4];
					
					status = Integer.parseInt(otherUsersLocations[i][5]);
					GeoPoint point = new GeoPoint(lat, lon);
					OverlayItem overlayitem2 = new OverlayItem(point, name,
							getMessage(point, speed, timestamp));
					// Then add it
					otherItemizedOverlay.addOverlay(overlayitem2, status);
					mapOverlays.add(otherItemizedOverlay);
				}
			}
		} else {
			// Do nothing
		}
	}

	/**
	 * @author Dan - Method to set TextView to how many users' are currently
	 *         sharing location information
	 */
	public void activeUsers() {
		if (users != null) {
			activeUsersTextView.setText(Integer.toString(activeUserCount) + " / "
						+ (users.size()));
			} else {
				activeUsersTextView.setText("0 / 0");
			}
	}

	public OtherItemizedOverlay getOtherItemisedOverlay() {
		return otherItemizedOverlay;
	}

	public LocationService getService() {
		return mService;
	}

	public GetUsersLocThread getGetLocationsThread() {
		return getLocationsThread;
	}

	public Handler getGetLocationsHandler() {
		return getLocationsHandler;
	}

	public EventModel getCurrentEvent() {
		return currentEvent;
	}

	public LocationService getMService() {
		return mService;
	}

	public void setBoundaryCentre(int lat, int lon) {
		boundaryCentre = new GeoPoint(lat, lon);
	}

	public void setBoundaryRadius(float radius) {
		boundaryRadius = radius;
	}

	public MapView getMapView() {
		return mapView;
	}
}