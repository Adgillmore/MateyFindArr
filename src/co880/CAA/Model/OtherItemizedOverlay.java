package co880.CAA.Model;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import co880.CAA.ServerUtils.EmailIntentManager;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class OtherItemizedOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context c;
	private String email;
	private Drawable markerGreen;
	private Drawable markerOrange;
	private Drawable markerRed;

	public OtherItemizedOverlay(Drawable defaultMarker, Drawable active, Drawable inactive, Context context) {
		super(boundCenterBottom(defaultMarker));
		c = context;
		markerOrange = defaultMarker;
		markerGreen = active;
		markerRed = inactive;
		markerRed.setBounds(-markerRed.getIntrinsicWidth()/2, -markerRed.getIntrinsicHeight(), markerRed.getIntrinsicWidth() /2, 0);
		markerGreen.setBounds(-markerGreen.getIntrinsicWidth()/2, -markerGreen.getIntrinsicHeight(), markerGreen.getIntrinsicWidth() /2, 0);
		
	}
	
	public void addOverlay(OverlayItem overlay, int state) {
		if (mOverlays.size() > 0) {

			Iterator i = mOverlays.iterator();
			while (i.hasNext()) {
				OverlayItem o = (OverlayItem) i.next();
				if (o.getTitle().equals(overlay.getTitle())) {
					i.remove();
				}
			}
		}
		overlay.setMarker(getMarker(state));
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	private Drawable getMarker(int state) {
		switch(state) {
		case 0: return markerGreen;
		case 1: return markerRed;
		default: return markerOrange;
		}
	}
	
	@Override
	protected boolean onTap(int i) {
		OverlayItem item = mOverlays.get(i);
		email = item.getTitle();
		AlertDialog.Builder dialog = new AlertDialog.Builder(c);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setNeutralButton("Email", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// if this button is clicked email the user
				// and close dialog
				String recepientEmail = getEmail();
				EmailIntentManager.launchEmailIntent(c, recepientEmail);
				dialog.cancel();
			}
		});
		dialog.show();
		return true;
	}
	
	public void clearOverlayItems() {
		if (mOverlays.size() > 0) {
			Iterator<OverlayItem> i = mOverlays.iterator();
			while (i.hasNext()) {
				OverlayItem item = i.next();
				i.remove();
			}
		}
	}
	
	private String getEmail() {
		return email;
	}
}
