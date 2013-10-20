package co880.CAA.Model;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import co880.CAA.Activities.CAAActivity;

/**
 * @author Dan - This class creates a notification object to be used when an event has ended.
 * It will be created by the SessionHandler which will also pass the LocationService in as a 
 * parameter
 */
public class TimeNotification {

	NotificationManager notMan;
	LocationService l;
	
	public TimeNotification(LocationService l) {
		this.l = l;
	}
	
	/**
	 * @author Dan - create the notification and send it
	 */
	public void createNotification() {
		final int ID = 1;
		
		String ns = Context.NOTIFICATION_SERVICE;
		notMan = (NotificationManager) l.getSystemService(ns);
		
		int icon = R.drawable.ic_menu_view;
		CharSequence tickerText = "Matey Find-Arrr!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = l.getApplicationContext();
		CharSequence contentTitle = "Event Expiry";
		CharSequence contentText = "You're current event has now finished, youy are no longer sharing your location";
		Intent notificationIntent = new Intent(l, CAAActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(l, 0, notificationIntent, Notification.FLAG_AUTO_CANCEL);
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notMan.notify(ID, notification);
	}
}
