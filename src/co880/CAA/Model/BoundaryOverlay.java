package co880.CAA.Model;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


/**
 * @author Adam
 * This class draws a blue circle on the clickableMapView to show a boundary
 * for the sharing event.
 * Based on code from Suraj at stack overflow
 * http://stackoverflow.com/questions/5293709/draw-a-circle-on-android-mapview
 * Modification the projection coversion taken from discussion on 
 * http://stackoverflow.com/questions/2077054/how-to-compute-a-radius-around-a-point-in-an-android-mapview
 */
public class BoundaryOverlay extends Overlay {

    private Context context;
    private int mLat;
    private int mLon;
    private float mRadius;

    /**
     * 
     * @param _context The context
     * @param _lat Latitude of the centre of the circle in microseconds
     * @param _lon Longitude of the centre of the circle in microseconds
     * @param radius Radius of the circle in metres
     */
     public BoundaryOverlay(Context _context, int _lat, int _lon, float radius ) {
            context = _context;
            mLat = _lat;
            mLon = _lon;
            mRadius = radius;
     }

     /**
      * The draw method called by mapView. This method converts a distance in pixels to
      * metres on the map (at the equator multiplied by a factor to correct for latitude).
      */
     public void draw(Canvas canvas, MapView mapView, boolean shadow) {
         super.draw(canvas, mapView, shadow); 

         if(shadow) return; // Ignore the shadow layer

         Projection projection = mapView.getProjection();

         Point pt = new Point();

         GeoPoint geo = new GeoPoint(mLat, mLon);

         projection.toPixels(geo, pt);
         float circleRadius = metersToRadius(mRadius, projection, mLat);

         Paint innerCirclePaint;

         innerCirclePaint = new Paint();
         innerCirclePaint.setColor(Color.BLUE);
         innerCirclePaint.setAlpha(50);
         innerCirclePaint.setAntiAlias(true);

         innerCirclePaint.setStyle(Paint.Style.FILL);

         canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, innerCirclePaint);
    }
     
    public static float metersToRadius(float requiredRadius, Projection projection, int latitude) {
    	    return (float) (projection.metersToEquatorPixels(requiredRadius) * (1/ Math.cos(Math.toRadians(latitude/1E6))));         
    }
}