package co880.CAA.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Dan Jones
 * This class contains methods to convert an epoch time to a traditional date and time
 * view, or vice versa
 *
 */
public class TimeConverter {

	/**
	 * Takes a time in the format of "MM/DD/YYYY HH:MM" and translates this into an epoch time
	 * @param String time
	 * @return String epochTime
	 */
	public String parseDateAndTime(String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date date = null;
		try {
			date = dateFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String newEpochTime = Long.toString(date.getTime());
		return newEpochTime;
	}
	
	/**
	 * Takes an epoch time stamp and translates into traditional date and time format 
	 * @param long timestamp
	 * @return String timestamp OR "N/A" if date equals null
	 */
	public String convertEpochToTimeFormat(long timestamp) {
		String time = null;
		String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
				.format(new java.util.Date(timestamp));
		if (date != null) {
			time = date.substring(11, 16);
			return time;
		} else {
			return "N/A";

		}
	}
	
	public static String convertEpochTimeToDateTime(long timeStamp) {
		String dateTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
		.format(new java.util.Date(timeStamp));
		return dateTime;
	}
	
	/**
	 * Constructs a string called date consisting of month, day and year
	 * @param int month
	 * @param int day
	 * @param int year
	 * @return String date
	 */
	public String dateAmalgamation(int month, int day, int year) {
		StringBuilder date = (new StringBuilder().append(month).append("/").append(day).append("/").append(year));
		return date.toString();
	}
	
	/**
	 * Constructs a string called time consisting of hour and minute
	 * @param String hour
	 * @param String min
	 * @return String time
	 */
	public String timeAmalgamation(String hour, String min) {
		StringBuilder time = (new StringBuilder().append(hour).append(":").append(min));
		return time.toString();
	}
}
