package co880.CAA.test;

import co880.CAA.Model.LocationService;
import android.location.Location;
import junit.framework.TestCase;

public class LocationServiceTest extends TestCase {
	
	private Location locObj;
	private Location locGPS;
	private Location locNet;
	private LocationService locService;
	
	public void setUp() throws Exception {
		locService = new LocationService();
		locObj = new Location("gps");
		locService.setLastGpsLocation(new Location("gps")); 
		locService.setLastNetworkLocation(new Location("network")); 
		locObj.setLatitude(51.28811042290181);
		locObj.setLongitude(1.063916478306055);
		/*
		locGPS.setTime(System.currentTimeMillis());
		locNet.setTime(1342114088490L);
		*/
		super.setUp();
	}

	public void testValidAge() {
		long newTime = 1342044567000L;
		long oldTime = 1342046567000L;
		assertTrue(locService.validAge(30000, newTime, oldTime));
	}
	
	/*
	public void testsetMyLocation1() {
		assertEquals(locService.setMyLocation(locGPS, locNet), locGPS);
	}
	*/
	
	public void testSetMyLocation1() {
		//Both locations are null
		locService.setMyLocation(null, null);
		assertNull(locService.getLocation());		
	}
	
	public void testSetMyLocation2() {
		//GPS is not valid, network is null
		locGPS = locService.getLastGpsLocation();
		locGPS.setTime(System.currentTimeMillis()-360000);
		locService.setGpsAge(120000);
		locObj = locService.setMyLocation(locGPS, null);
		assertNull(locObj);		
	}
	
	public void testSetMyLocation3() {
		//GPS is not valid, network is not null
		locGPS = locService.getLastGpsLocation();
		locNet = locService.getLastGpsLocation();
		locGPS.setTime(System.currentTimeMillis()-360000);
		locNet.setTime(System.currentTimeMillis());
		locService.setGpsAge(120000);
		locObj = locService.setMyLocation(locGPS, locNet);
		assertSame(locObj, locNet);		
	}
	
	public void testSetMyLocation4() {
		//GPS is valid but not accurate, network is not null
		locGPS = locService.getLastGpsLocation();
		locNet = locService.getLastGpsLocation();
		locGPS.setTime(System.currentTimeMillis());
		locGPS.setAccuracy(150.0f);
		locNet.setTime(System.currentTimeMillis());
		locNet.setAccuracy(25.0f);
		locService.setGpsAge(120000);
		locObj = locService.setMyLocation(locGPS, locNet);
		assertSame(locObj, locNet);		
	}
	
	public void testSetMyLocation5() {
		//GPS is valid and accurate, network is not null
		locGPS = locService.getLastGpsLocation();
		locNet = locService.getLastGpsLocation();
		locGPS.setTime(System.currentTimeMillis());
		locGPS.setAccuracy(25.0f);
		locNet.setTime(System.currentTimeMillis());
		locNet.setAccuracy(150.0f);
		locService.setGpsAge(120000);
		locObj = locService.setMyLocation(locGPS, locNet);
		assertSame(locObj, locGPS);		
	}
	
	public void testSetMyLocation6() {
		//GPS is valid and accurate, network is null
		locGPS = locService.getLastGpsLocation();
		locGPS.setTime(System.currentTimeMillis());
		locGPS.setAccuracy(25.0f);
		locService.setGpsAge(120000);
		locObj = locService.setMyLocation(locGPS, null);
		assertSame(locObj, locGPS);		
	}
	
	
}
