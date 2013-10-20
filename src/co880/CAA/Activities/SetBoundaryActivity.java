package co880.CAA.Activities;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import co880.CAA.R;
import co880.CAA.Model.BoundaryOverlay;
import co880.CAA.Model.ClickableMapView;
import co880.CAA.Model.MyItemizedOverlay;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;


/**
 * @author Adam
 * This class provides the activity for a user to determine the centre of a boundary using longTap
 * and the radius of the boundary using a seekbar. The results of the activity are returned
 * to either CreateEvent or EditEvent via a Bundle.
 */
public class SetBoundaryActivity extends MapActivity {

	private ClickableMapView map;
	private SeekBar bar;
	private List<Overlay> mapOverlays;
	private MyItemizedOverlay myItemizedOverlay;
	private Drawable drawable;
	private SetBoundaryActivity setBoundary;
	private BoundaryOverlay boundary;
	private OnSeekBarChangeListener barListener;
	private float radius;
	private TextView boundaryText;
	private Button saveButton;
	private int latitude;
	private int longitude;
	private Bundle boundaryData;
	private static final String TAG = "SetBoundaryActivity";
	private LocationManager locMgr;
	private Location currentLocation;
	private NetworkLocationListener netListener;
	private MapController controller;
	private static final int BOUNDARY_RANGE = 2500/100; //25 m per percentage point on the progress bar
	private static final int BAR_INITIAL_POSITION = 50;
	private GeoPoint myGeoPoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_boundary);
				
		//Centre the map on the user's current location for convenience
		//Doesn't require the LocationService to be running.
		locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		currentLocation = locMgr
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		netListener = new NetworkLocationListener();
		locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0, 0, netListener);
		
		if (currentLocation != null) {
			//Set initial values for the boundary so it is visible to the user
			latitude = (int) (currentLocation.getLatitude()*1E6);
			longitude = (int) (currentLocation.getLongitude()*1E6);
			myGeoPoint = new GeoPoint(latitude, longitude);
		} else {
			//Arbitrary values in case location can't be determined
			latitude = 51298483;
			longitude = 1070658;
			myGeoPoint = new GeoPoint(latitude, longitude);
		}

		setBoundary = this;
		radius = BOUNDARY_RANGE*BAR_INITIAL_POSITION;

		//Set up the GUI
		map = (ClickableMapView) findViewById(R.id.mapView);
		map.setBuiltInZoomControls(true);
		controller = map.getController();
		controller.setZoom(15);
		controller.setCenter(myGeoPoint);
		bar = (SeekBar) findViewById(R.id.seekBar1);
		boundaryText = (TextView) findViewById(R.id.textView4);
		saveButton = (Button) findViewById(R.id.button1);
		boundaryText.setText("Radius: " + radius + " m");
		
		//Set up the overlays
		mapOverlays = map.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.marker);
		myItemizedOverlay = new MyItemizedOverlay(drawable, this);
		OverlayItem overlayItem = new OverlayItem(myGeoPoint, "Current Location", "You are here");
		myItemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(myItemizedOverlay);
		boundary = new BoundaryOverlay(this, latitude, longitude, 0);

		//Set up the SeekBar
		barListener = new SeekBarListener();
		bar.setOnSeekBarChangeListener(barListener);
		bar.setProgress(BAR_INITIAL_POSITION);
		
		//Set the Listeners
		saveButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				boundaryData = new Bundle();
				boundaryData.putInt("latitude", latitude);
				boundaryData.putInt("longitude",  longitude);
				boundaryData.putFloat("radius",  radius);
				Intent resultIntent = new Intent();
				resultIntent.putExtra("boundaryData", boundaryData);
				setResult(RESULT_OK, resultIntent); 
				setBoundary.finish();
			}
		});
		
		map.setOnLongTapListener(new ClickableMapView.OnLongTapListener() {
			
	        public void onLongTap(final MapView view, final GeoPoint longTapLocation) {
	            runOnUiThread(new Runnable() {
	            public void run() {
	                latitude = longTapLocation.getLatitudeE6();
	                longitude = longTapLocation.getLongitudeE6();
	                Log.i(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
	                redrawBoundary();
	            }
	        });
	        }
	    });
	}
	
	@Override
	protected void onPause() {
		locMgr.removeUpdates(netListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0, 0, netListener);
		super.onResume();
	}

	@Override
	protected void onRestart() {
		locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0, 0, netListener);
		super.onRestart();
	}

	@Override
	protected void onStop() {
		locMgr.removeUpdates(netListener);
		super.onStop();
	}

	public class SeekBarListener implements OnSeekBarChangeListener {

		public void onStopTrackingTouch(SeekBar seekBar) {
			radius = seekBar.getProgress()*BOUNDARY_RANGE;
			boundaryText.setText("Radius: " + radius + " m");
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			redrawBoundary();
			radius = seekBar.getProgress()*BOUNDARY_RANGE;
			boundaryText.setText("Radius: " + radius + " m");
		}
	}
	
	private void redrawBoundary() {
		if (mapOverlays.contains(boundary)) {
			mapOverlays.remove(boundary);
		}
		boundary = new BoundaryOverlay(setBoundary, latitude, longitude,
				(float) bar.getProgress()*BOUNDARY_RANGE);
		mapOverlays.add(boundary);
		map.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	
	public class NetworkLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			currentLocation = loc;
		}

		public void onProviderDisabled(String provider) {			
		}

		public void onProviderEnabled(String provider) {			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {			
		}
	}
}