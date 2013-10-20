package co880.CAA.test;

import com.google.android.maps.GeoPoint;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

/**
 * @author KaLan
 * 
 */

public class LocationActivityTest extends ActivityInstrumentationTestCase2<LocationActivity> {
	private LocationActivity locAct;
	private Location locObj;
	private Location locGPS;
	private Location locNet;
	private CAAActivity caaAct;
	private SharedPreferences pref;
	private CalendarModel myCalendar;

	public LocationActivityTest() {
		super("co880.CAA.Activities", LocationActivity.class);
		
	}

	public void setUp() throws Exception {
		
		
		locAct = getActivity();
		locObj = new Location("gps");
		locGPS = new Location("gps");
		locNet = new Location("network");
		locObj.setLatitude(51.28811042290181);
		locObj.setLongitude(1.063916478306055);
		locGPS.setTime(System.currentTimeMillis());
		locNet.setTime(1342114088490L);
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testLocationToGeopoint() {
		GeoPoint geo = new GeoPoint(51288110, 1063916);
		assertEquals(geo, Utils.locationToGeopoint(locObj));
	}
	
	public void testconvertEpochToTimeFormat() {
		long timeStamp = 1342118116927L;
		assertEquals("19:35:16" , Utils.convertEpochToTimeFormat(timeStamp));
	}

	
}
