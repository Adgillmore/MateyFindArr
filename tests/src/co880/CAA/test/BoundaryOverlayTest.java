package co880.CAA.test;

import com.google.android.maps.MapView;

import android.graphics.Canvas;
import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.Model.BoundaryOverlay;

public class BoundaryOverlayTest extends ActivityInstrumentationTestCase2<LocationActivity>  {

	private BoundaryOverlay bo;
	private LocationActivity locAct;
	
	public BoundaryOverlayTest() {
		super("co880.CAA.Activities", LocationActivity.class);
	}

	protected void setUp() throws Exception {
		locAct = getActivity();
		bo = new BoundaryOverlay(locAct, 51298483, 1070658, 50.0f);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMetersToRadius() {
		MapView m = locAct.getMapView();
		//Canvas canvas = new Canvas();
		//bo.draw(canvas, m, false);
		assertEquals(0f, (bo.metersToRadius(50.0f, m.getProjection(), 51298483)));
		//assertEquals(1f, bo.getCircleRadius());
	}
}
