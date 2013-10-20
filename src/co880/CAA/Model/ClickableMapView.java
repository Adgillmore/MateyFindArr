package co880.CAA.Model;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

//import android.view.GestureDetector.OnLongTapListener;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


/**
* @author Adam
* Based on tutorial from Roger Kind Kristiansen
* http://www.kind-kristiansen.no/2011/android-handling-longpresslongclick-on-map-revisited/
**/
public class ClickableMapView extends MapView {
	
    private static final int LONGTAP_THRESHOLD = 500;
	private ClickableMapView.OnLongTapListener longTapListener;
    private GeoPoint lastMapCenter;
    private Timer longTapTimer;
    private static final String TAG = "ClickableMapView";

    public ClickableMapView(Context context, String apiKey) {
        super(context, apiKey);
    }
 
    public ClickableMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public ClickableMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
	public interface OnLongTapListener {
		public void onLongTap(MapView view, GeoPoint longTapLocation);
	}
 
    public void setOnLongTapListener(ClickableMapView.OnLongTapListener listener) {
        longTapListener = listener;
    }
     
    /**
     * This method is called every time user touches the map,
     * drags a finger on the map, or removes finger from the map.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleLongTap(event);
         
        return super.onTouchEvent(event);
    }
 
    /**
     * This method takes MotionEvents and decides whether or not
     * a LongTap has been detected. This is the meat of the
     * OnLongTapListener.
     * We then listen for map movements or the finger being
     * removed from the screen. If any of these events occur
     * before the TimerTask is executed, it gets cancelled. Else
     * the listener is fired.
     *  
     * @param event
     */
    private void handleLongTap(final MotionEvent event) {
         Log.i(TAG, "longTap");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Finger has touched screen.
        	
        	longTapTimer = new Timer();
            
            longTapTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                	int version = Build.VERSION.SDK_INT;
                	GeoPoint longTapLocation;
                	if (version > 10) {
                		longTapLocation = getProjection().fromPixels((int)event.getX(), 
                            (int)event.getY()-200);//Fixes differences with how the point is calculated post-Gingerbread.
                	} else {
                		longTapLocation = getProjection().fromPixels((int)event.getX(), 
                                (int)event.getY());
                	}
                     
                    /*
                     * Fire the listener. We pass the map location
                     * of the longTap as well, in case it is needed
                     * by the caller.
                     */
                    longTapListener.onLongTap(ClickableMapView.this, longTapLocation);
                }
                 
            }, LONGTAP_THRESHOLD);
             
            lastMapCenter = getMapCenter();
        }
         
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                 
            if (!getMapCenter().equals(lastMapCenter)) {
                // User is panning the map, this is no longTap
                longTapTimer.cancel();
            }
             
            lastMapCenter = getMapCenter();
        }
         
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // User has removed finger from map.
            longTapTimer.cancel();
        }
 
            if (event.getPointerCount() > 1) {
                        // This is a multitouch event, probably zooming.
                longTapTimer.cancel();
            }
    }
}

