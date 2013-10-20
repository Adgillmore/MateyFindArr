package co880.CAA.test;

import com.google.android.maps.GeoPoint;

import co880.CAA.Model.BoundaryCheck;
import co880.CAA.Model.Utils;
import android.location.Location;
import android.test.AndroidTestCase;

public class BoundaryCheckTest extends AndroidTestCase {
	
	private BoundaryCheck bc;
	private Utils utils;

	public BoundaryCheckTest() {
		super();
		Location loc = new Location("test");
		loc.setLatitude(51.567795);
		loc.setLongitude(0.444556);
		GeoPoint g = utils.locationToGeopoint(loc);
		bc = new BoundaryCheck(g, 150.00f);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInBoundaryTrue() {
		Location loc = new Location("test");
		loc.setLatitude(51.567799);
		loc.setLongitude(0.444559);
		assertEquals(true, bc.inBoundary(loc));
	}
	
	public void testInBoundaryFalse() {
		Location loc = new Location("test");
		loc.setLatitude(48.64017);
		loc.setLongitude(4.10889);
		assertEquals(false, bc.inBoundary(loc));
	}
}
