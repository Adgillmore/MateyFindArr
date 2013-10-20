package co880.CAA.Model;

import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author Adam
 * Handles messages from the SessionThread and passes 
 * the data to the eventModel for updating the time display and 
 * session 'eventActive' boolean.
 */
public class SessionHandler extends Handler {
	private EventModel eventModel;
	private LocationService l;

	public SessionHandler(EventModel inEvent, LocationService l) {
		eventModel = inEvent;
		this.l = l;
	}
	
	@Override
	public void handleMessage(Message message) {
		if(message.what == 13) {
			eventModel.setEventActive(message.getData().getBoolean("eventActive"));
			TimeNotification t = new TimeNotification(l);
			t.createNotification();
			eventModel.checkEventIsActive();
		} else if (message.what == 0) {
			eventModel.updateTimeDisplay(message.getData().getLong("timeDifference"));
		}
	}
}
