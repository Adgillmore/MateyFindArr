package co880.CAA.Model;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author Adam
 * This class is an abstract class which provides
 * the method header for checking the expiry time.
 */
public abstract class ExpiryChecker {

		private Context ctx;
		private int eventID;
		private HashMap<String, String> endTime;
		private CalendarModel myCalendar;
		private boolean eventActive;

		/**
		 * constructor which automatically retrieves the end time
		 * @param inCtx
		 * @param inEventID
		 */
		public ExpiryChecker(Context inCtx, int inEventID) {
			ctx = inCtx;
			eventID = inEventID;
			myCalendar = new CalendarModel(ctx);
			setEndTime(myCalendar.getEndTime(eventID));
		}

		public abstract void checkExpiryTime(); 
		
		public long getEndTime() {
			long time;
			if (endTime != null) {
				time = Long.parseLong(endTime.get("dtend"));
			} else {
				time = 0;
			}
			return time;
		}
		
		/**
		 * A method to reset the end time based on a specified eventID 
		 * instead of setting it from the eventID stored in the field.
		 * @param eventID
		 */
		public void resetEndTimeFromEvent(int eventID) {
			setEndTime(myCalendar.getEndTime(eventID));
		}
		
		public HashMap<String, String> returnEndTimeMap() {
			return endTime;
		}
		
		public void setEndTime(HashMap<String, String> endTime) {
			this.endTime = endTime;
		}
		
		public void setEventActive(boolean eventActive) {
			this.eventActive = eventActive;
		}
		
		public boolean isEventActive() {
			return eventActive;
		}

		public int getEventID() {
			return eventID;
		}

		public void setEventID(int eventID) {
			this.eventID = eventID;
		}

		public CalendarModel getMyCalendar() {
			return myCalendar;
		}
}
