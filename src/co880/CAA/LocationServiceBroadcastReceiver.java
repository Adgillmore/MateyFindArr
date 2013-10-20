package co880.CAA;

import co880.CAA.Activities.LocationActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Primary: Dan. Additional: Adam & Ka
 * The BroadcastReciever which receives broadcasts from LocationService and
 * directs them to LocationActivity onLocationChanged
 */
public class LocationServiceBroadcastReceiver extends BroadcastReceiver {

	private LocationActivity a;
	private Location location;
	private static final String TAG = "LocationServiceBroadcastReceiver";

	public LocationServiceBroadcastReceiver() {
		super();
	}

	/**
	 * @author Dan - Mutator to set the LocationActivity in order to access
	 * its methods
	 * @param a
	 */
	public void setActivity(LocationActivity a) {
		this.a = a;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent Recieved: " + intent.toString());
		Bundle bundle = intent.getBundleExtra("locationB");
		location = bundle.getParcelable("locationP");
		// If the LocationAcivity is initialized draw the new location
		if (a != null) {
			a.drawMyLocation(location);
			a.getMapView().invalidate();
			if (a.getMService() != null) {
				if (a.getMService().getBoundaryChecker() != null) {// Initialized
																	// in
																	// OnStart
					if (a.getMService().getBoundaryChecker()
							.inBoundary(location)) {
						a.getUsersLocations();
					} else {
						Log.i(TAG, "outside boundary");
						if (a.getLocationsThread != null) {
							a.getLocationsThread.setQueryServer(false);
							a.setOtherUsersLocs(null);
							a.getOtherItemisedOverlay().clearOverlayItems();
							a.getMapView().invalidate();
							Log.i(TAG, "stopped requesting other users");
						}
					}

				} else {
					a.getUsersLocations();
				}
			}
		}
	}
}