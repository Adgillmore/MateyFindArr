package co880.CAA.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


/**
 * 
 * Main reference is from android developer's guide https://developers.google.com/maps/documentation/android/hello-mapview.
 *
 */

@SuppressWarnings("rawtypes")
public class MyItemizedOverlay extends ItemizedOverlay {
	
	private OverlayItem mOverlay;
	private Context c;
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		c = context;
	}
	
	/**
	 * By using just an item we will overwrite it every time.
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) {
		mOverlay = overlay; 
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlay;
	}

	@Override
	public int size() {
		return 1;
	}
	
	//Override to display dialog with relevant details.
	@Override
	protected boolean onTap(int i) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(c);
		dialog.setTitle(mOverlay.getTitle());
		dialog.setMessage(mOverlay.getSnippet());
		dialog.show();
		return true;
	}
}
