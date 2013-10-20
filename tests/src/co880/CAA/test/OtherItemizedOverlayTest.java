package co880.CAA.test;

import java.util.ArrayList;

/**
 * @author Ka Lan 
 * Junit android test class that tests the OtherItemizedOverlay class,
 * just to test if there are overlap in overlays.
 */

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import co880.CAA.R;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.OtherItemizedOverlay;
import android.test.ActivityInstrumentationTestCase2;

public class OtherItemizedOverlayTest extends
		ActivityInstrumentationTestCase2<LocationActivity> {

	private LocationActivity locAct;
	private OtherItemizedOverlay otherOverlay;
	private ArrayList<OverlayItem> mOverlays;
	private OverlayItem[] overlayItems;
	private OverlayItem o;

	public OtherItemizedOverlayTest() {
		super("co880.CAA.Activities", LocationActivity.class);
	}

	protected void setUp() throws Exception {
		locAct = getActivity();
		mOverlays = new ArrayList<OverlayItem>();
		overlayItems = new OverlayItem[2];
		otherOverlay = new OtherItemizedOverlay(locAct.getResources()
				.getDrawable(R.drawable.marker_amber), locAct.getResources()
				.getDrawable(R.drawable.marker_green), locAct.getResources()
				.getDrawable(R.drawable.marker_red), locAct.getApplicationContext());
		overlayItems[0] = new OverlayItem(new GeoPoint(51291436, 1065340),
				"mimicdanjay@googlemail.com", "Distance: 853 m Speed: 1 mph");
		overlayItems[1] = new OverlayItem(new GeoPoint(11111111, 2222222),
				"copydanjay@googlemail.com", "Distance: 666 m Speed: 2 mph");
		for (OverlayItem overlayItem : overlayItems) {
			mOverlays.add(overlayItem);
		}
		o = overlayItems[0];
		for (int i = 0; i < mOverlays.size(); i++) {
			otherOverlay.addOverlay(mOverlays.get(i), 0);
		}
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	//Tests for overlap.
	public void testAddOverlay() {
		otherOverlay.addOverlay(o, 0);
		assertEquals(2, otherOverlay.size());
	}

}
