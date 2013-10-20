package co880.CAA.Model;

import com.google.android.maps.GeoPoint;

import android.location.Location;

/**
 * 
 * @author Adam
 * This class takes in a Location object and determines whether or not
 * it is within a boundary defined in the constructor.
 *
 */
public class BoundaryCheck {
	private Location circleCentre;
	private float radius;
	
	public BoundaryCheck(GeoPoint inCentrePoint, float inRadius) {
		circleCentre = Utils.geoPointToLocation(inCentrePoint);
		radius = inRadius;
	}

	public boolean inBoundary(Location myLocation) {
		float myRadius = (myLocation.distanceTo(circleCentre));
		if (myRadius <= radius) {
			//we're inside the boundary
			return true;
		} else {
		return false;
		}
	}	
}
