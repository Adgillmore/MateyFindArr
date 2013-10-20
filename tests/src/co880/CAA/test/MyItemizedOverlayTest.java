package co880.CAA.test;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.R;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.MyItemizedOverlay;

/**
 * 
 * @author Ka lan
 *Junit Android test class for myItemizedOverlay.
 *Just to see if the overlay overwrite is working correctly.
 */

public class MyItemizedOverlayTest extends ActivityInstrumentationTestCase2<LocationActivity> {
	
	private MyItemizedOverlay myItem;
	private OverlayItem mOverlay;
	private OverlayItem mOverlay2;

	public MyItemizedOverlayTest() {
		super("co880.CAA.Activities", LocationActivity.class);
	}

	protected void setUp() throws Exception {
		LocationActivity locAct = getActivity();
		myItem = new MyItemizedOverlay(locAct.getResources()
				.getDrawable(R.drawable.marker), locAct.getApplicationContext());
		mOverlay = new OverlayItem(new GeoPoint(51291436, 1065340),
				"mimicdanjay@googlemail.com", "Distance: 853 m Speed: 1 mph");
		mOverlay2 = new OverlayItem(new GeoPoint(11111111, 2222222),
				"copydanjay@googlemail.com", "Distance: 666 m Speed: 2 mph");
		myItem.addOverlay(mOverlay);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	// Should be one as every new overlay item will overwrite the last one.
	public void testAddOverlay() {
		myItem.addOverlay(mOverlay2);
		assertEquals(1, myItem.size());
	}

}
