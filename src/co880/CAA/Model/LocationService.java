package co880.CAA.Model;

import com.google.android.maps.GeoPoint;

import co880.CAA.ServerUtils.SendLocationData;
import co880.CAA.ServerUtils.SetSharingStatus;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Primary: Dan. Additional: Adam & Ka
 * This Service is responsible for sending a users' location to the
 * server whilst running in the background
 */
public class LocationService extends Service {

	private final String TAG = "LocationService";
	private final IBinder mBinder = new LocalBinder();
	private LocationManager locMgr;
	private LocationListener netList;
	private LocationListener gpsList;
	private Location lastGpsLocation;
	private Location lastNetworkLocation;
	private long gpsAge;
	private int updateFrequency;
	private Location location;
	private GeoPoint myGeoPoint;
	private String email;
	private SharedPreferences pref;
	private Integer mySpeed;
	private EventModel eventModel;
	private BoundaryCheck boundaryChecker;
	private static final int INACTIVE = 1;
	private static final int ACTIVE = 0;

	/**
	 * @author Dan Class used for the client Binder.
	 */
	public class LocalBinder extends Binder {
		public LocationService getService() {
			// Return this instance of LocationService so clients can call
			// public methods
			return LocationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent i) {
		return mBinder;
	}

	@SuppressLint("WorldReadableFiles")
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Created LocationService");

		// Instantiate location manager and listener.
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		netList = new networkLocationListener();
		gpsList = new gpsLocationListener();

		// Retrieve last known location from both providers
		lastGpsLocation = locMgr
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		lastNetworkLocation = locMgr
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// Retrieve email from shared preferences
		pref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
		email = pref.getString("email",
				"Error at LocationActivity pref.getString");

		// Retrieve update frequency from shared preferences
		SharedPreferences updatePref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String updateFreq = (updatePref.getString("listPref", "60000"));
		int updateF = Integer.valueOf(updateFreq);
		setUpdateFrequency(updateF);

		// Assigns GPS age limit to X.
		setGpsAge(120000);

		// Set location and register for updates
		location = setMyLocation(lastGpsLocation, lastNetworkLocation);
		processMyLocation(location);
		locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				updateFrequency, 0, netList);
		locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				updateFrequency, 0, gpsList);
	}

	/**
	 * @author Dan - method which sets up EventModel and creates event thread
	 * @param EvenetModel
	 *            - EventModel of current event, used to create a session thread
	 */
	public void setEventModel(EventModel e) {
		eventModel = e;
		eventModel.runSessionThread(this);
	}
	
	/**
	 * @author Adam - Instatiate a boundaryChecker object against which to
	 * 	judge whether a user has left
	 * @param GeoPoint boundaryCentre - centre point of defined boundary
	 * @param float boundaryRadius - radius of defined boundary
	 */
	public void initiateBoundary(GeoPoint boundaryCentre, float boundaryRadius) {
		if (eventModel.getBoundarySet()) {
			boundaryChecker = new BoundaryCheck(boundaryCentre, boundaryRadius);
		}
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "LocationService started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "LocationService destroyed");

		// Stop updates for location
		locMgr.removeUpdates(gpsList);
		locMgr.removeUpdates(netList);
	}

	/**
	 * @author Ka Lan - Compares last known locations and picks most appropriate
	 *         based on accuracy, time, etc.
	 * @param locationGPS
	 *            - found via GPS listener
	 * @param locationNetwork
	 *            - found via Network listener
	 * @return Location - best location to use
	 */
	public Location setMyLocation(Location locationGPS, Location locationNetwork) {
		// Check validity (age) of GPS location
		boolean validGPS;
		if (locationGPS != null) {
			validGPS = validAge(gpsAge, System.currentTimeMillis(),
					locationGPS.getTime());
		} else {
			validGPS = true;
		}
		
		//Obtain accuracy estimates for each location
		float accuracyGPS;
		float accuracyNetwork;
		if (locationGPS != null) {
			accuracyGPS = locationGPS.getAccuracy();
			//Toast.makeText(this, "GPS accuracy " + accuracyGPS, Toast.LENGTH_SHORT).show();
		} else {
			accuracyGPS = 0;
		}
		if (locationNetwork != null) {
			accuracyNetwork = locationNetwork.getAccuracy();
			//Toast.makeText(this, "Network accuracy " + accuracyNetwork, Toast.LENGTH_SHORT).show();
		} else {
			accuracyNetwork = 0;
		}

		// Decide which location to use
		if ((locationGPS == null || !validGPS) && locationNetwork == null) {
			return null;
		} else if ((locationGPS == null || !validGPS)
				&& locationNetwork != null) {
			return locationNetwork;
		} else if (validGPS && (accuracyGPS > accuracyNetwork)) {
			return locationNetwork;			
		} else if (validGPS) {
			return locationGPS;
		} else {
			return null;
		}

	}

	/**
	 * @author Dan - Method to take location object and send it to
	 *         LocationActivity, then send it to server and log whether there is
	 *         a boundary defined or not
	 * @param location
	 *            - to process
	 */
	public void processMyLocation(Location location) {
		if (location != null) {
			// Which provider did our location come from?
			Toast.makeText(getBaseContext(), "" + location.getProvider(),
					Toast.LENGTH_LONG).show();

			myGeoPoint = Utils.locationToGeopoint(location);
			Bundle b = new Bundle();
			b.putParcelable("locationP", location);
			Intent intent = new Intent("co880.CAA.NEW_LOCATION");
			intent.putExtra("locationB", b);
			sendBroadcast(intent);
			Log.d(TAG, "Intent sent form service: " + intent.toString());

			// Check whether a boundary has been set and, if so, whether users'
			// location is within it
			if (eventModel != null) { // Location activity has therefore
										// finished initialising.
				if ((eventModel.hasBoundary() && boundaryChecker
						.inBoundary(location))) {
					Log.i(TAG, "within boundary");
					myLocationToServer();
				} else if (!eventModel.hasBoundary()) {
					Log.i(TAG, "No boundary defined");
					myLocationToServer();
				} else {
					//change status to inactive
					setStatus(INACTIVE);
				}
			}
		} else {
			// Location is null so Toast waiting and try again later
			Toast.makeText(getBaseContext(), "Waiting for Location",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @author Adam - Sends user's location to the server using an AsyncTask
	 */
	private void myLocationToServer() {
		if (location.hasSpeed()) {
			mySpeed = Math.round(location.getSpeed());
		} else {
			mySpeed = 0;
		}
		SendLocationData upload = new SendLocationData(this);
		upload.execute(email, Integer.toString(myGeoPoint.getLatitudeE6()),
				Integer.toString(myGeoPoint.getLongitudeE6()), Integer.toString(mySpeed),
				Long.toString(location.getTime()), Integer.toString(ACTIVE));
	}
	
	/**
	 * @author Adam
	 * Changes the user's sharing status between active or inactive
	 * in order to set the map marker colour.
	 * @param status an int, 0 = active, 1 = inactive.
	 */
	private void setStatus(int status) {
		SetSharingStatus upload = new SetSharingStatus(this);
		upload.execute(email, Integer.toString(status));
	}

	/**
	 * @author Ka Lan - Method to establish if location time is of a valid age
	 * @param limit
	 *            - The time limit that you would want to compare the two
	 *            location times.
	 * @param newer
	 *            - the time in milliseconds UTC for the newer location.
	 * @param older
	 *            - the time in milliseconds UTC for the older location.
	 * @return boolean - true if it is more recent depending on the limit or
	 *         false if not.
	 */
	public boolean validAge(long limit, long newer, long older) {
		if ((newer - older) <= (limit)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @author Dan - Overidding onLocationChanged from
	 *         LocationListener class.
	 */
	public class gpsLocationListener implements LocationListener {

		/**
		 * @author Dan - Logic to determine when to use the GPS
		 *         location depending on current state.
		 */
		public void onLocationChanged(Location loc) {
			lastGpsLocation = loc;
			
			// This is the method which contains all the logic
			location = setMyLocation(loc, lastNetworkLocation);
			
			// Handle the result
			processMyLocation(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * @author Dan - Overidding onLocationChanged from
	 *         LocationListener class.
	 */
	public class networkLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			lastNetworkLocation = loc;
			
			// This is the method which contains all the logic
			location = setMyLocation(lastGpsLocation, loc);
			
			// Handle the result
			processMyLocation(location);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public long getGpsAge() {
		return gpsAge;
	}

	public void setGpsAge(long gpsAge) {
		this.gpsAge = gpsAge;
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public BoundaryCheck getBoundaryChecker() {
		return boundaryChecker;
	}

	public EventModel getEventModel() {
		return eventModel;
	}
	
	public Location getLastNetworkLocation() {
		return lastNetworkLocation;
	}

	public Location getLastGpsLocation() {
		return lastGpsLocation;
	}

	public void setLastGpsLocation(Location lastGpsLocation) {
		this.lastGpsLocation = lastGpsLocation;
	}

	public void setLastNetworkLocation(Location lastNetworkLocation) {
		this.lastNetworkLocation = lastNetworkLocation;
	}
}