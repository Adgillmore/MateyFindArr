package co880.CAA.Model;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class Utils {

	/**
	 * @author Adam
	 * A class containing commonly used utility methods typically for 
	 * calculations and data manipulation.
	 */
	
	public Utils() {
	}
	
	/**
	 * @author Dan - convert epoch time to a more readable time String
	 * @param timestamp
	 *            - to convert to time format
	 * @return String - time in HH:MM:SS format or 'N/A'
	 */
	public static String convertEpochToTimeFormat(long timestamp) {
		String time = null;
		String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
				.format(new java.util.Date(timestamp));
		if (date != null) {
			time = date.substring(11);
			return time;
		} else {
			return "N/A";
		}
	}
	
	/**
	 * @author Dan - simple method to convert location object to geopoint object
	 * @param location - to convert to geopoint
	 * @return geopoint - converted location object
	 */
	public static GeoPoint locationToGeopoint(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lon = (int) (location.getLongitude() * 1E6);
		return new GeoPoint(lat, lon);
	}
	
	/**
	 * @author Ka Lan - Work out the distance between user and friend.
	 * @param GeoPoint
	 *            - Other location which own distance is measured against
	 * @return int Distance - either distance or 0 is unavailable
	 */
	public static int getDistance(GeoPoint p, Location location) {
		float lat = (p.getLatitudeE6() / 1000000F);
		float lon = (p.getLongitudeE6() / 1000000F);
		Location otherLoc = new Location("Other User's Location");
		Location myLoc = location;
		float dis;
		otherLoc.setLatitude(lat);
		otherLoc.setLongitude(lon);
		if (myLoc != null) {
			dis = myLoc.distanceTo(otherLoc);
		} else {
			dis = 0;
		}
		return (int) dis;
	}
	
	public static Location geoPointToLocation(GeoPoint point) {
		Location convertedGeoPoint = new Location("converted");
		double lat = point.getLatitudeE6()/1E6;
		double longitude = point.getLongitudeE6()/1E6;
		convertedGeoPoint.setLatitude(lat);
		convertedGeoPoint.setLongitude(longitude);
		return convertedGeoPoint;
	}
}
