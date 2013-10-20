package co880.CAA.Model;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Ka lan.
 * This class uses EventListHelper to deal with the SQLiteDatabase.
 * The main reference for this class is the Lars Vogel blog http://www.vogella.com/articles/AndroidSQLite/article.html.
 */
public class MyEventDb {

	private EventListHelper elHelper;
	private SQLiteDatabase database;
	private Context context;
	private String[] allColumns = {EventListHelper.EVENT_ID, EventListHelper.BOUNDARY_RADIUS, EventListHelper.BOUNDARY_LATITUDE, EventListHelper.BOUNDARY_LONGITUDE};
	
	public MyEventDb(Context nContext) {
		context = nContext;
		elHelper = new EventListHelper(context);
	}
	
	public void closeOld() {
		elHelper.close();
	  }
	
	public void close() {
		database.close();
	  }
	
	public void open() throws SQLException {
	    database = elHelper.getWritableDatabase();
	  }
	
	public void openRead() throws SQLException {
		database = elHelper.getReadableDatabase();
	}

	public void insertEvent(int eventID, int radius, int lat, int lon) {
		ContentValues values = new ContentValues();
		values.put(EventListHelper.EVENT_ID, eventID);
		values.put(EventListHelper.BOUNDARY_RADIUS, radius);
		values.put(EventListHelper.BOUNDARY_LATITUDE, lat);
		values.put(EventListHelper.BOUNDARY_LONGITUDE, lon);
		@SuppressWarnings("unused")
		long result = database.insert(EventListHelper.EVENT_TABLE_NAME, null, values);
	}
	
	/**
	 * @author Ka lan
	 * @return list of eventIds from our local database.
	 */
	public ArrayList<Integer> getEventsIds() {
		int[][] data = getAllEventDetails();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < 1; j++) {
				list.add(data[i][j]);
			}
		}
		return list;
	}
	
	public Cursor queryEvent(int eventID) {
		Cursor cursor = database.query(EventListHelper.EVENT_TABLE_NAME, new String[] {EventListHelper.BOUNDARY_LATITUDE, EventListHelper.BOUNDARY_LONGITUDE, EventListHelper.BOUNDARY_RADIUS}, EventListHelper.EVENT_ID + "= '" + eventID + "'", null, null, null, null);
		return cursor;
	}

	/**
	 * @author Ka lan
	 * method to return details into a 2-dimensional array.
	 * @return
	 */
	public int[][] getAllEventDetails() {
		Cursor cursor = database.query(EventListHelper.EVENT_TABLE_NAME,
				allColumns, null, null, null, null, null);
		 int[][] twoDValue = new int[cursor.getCount()][4];
		// Cursor cursor = eventDB.query("EventList", new String[] {"Event_ID"},
		// "Event_type = 'Timed'", null, null, null, "Event_ID");
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			for (int j = 0; j < 4; j++) {
				twoDValue[i][j] = cursor.getInt(j);
				}
			cursor.moveToNext();
			}
		cursor.close();
		return twoDValue;
	}

	/*public void displayEventDetails(ArrayList<Integer> list) {
		StringBuilder sb = new StringBuilder();
		ArrayList<String> displayStringList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, String> eventMap = myCalendar.getEventDetails(list
					.get(i));
			sb.append(eventMap.get("title"));
			sb.append("\nStart: ");
			sb.append(eventMap.get("dtstart"));
			sb.append(eventMap.get(", Finish: "));
			sb.append(eventMap.get("dtend"));
			String displayString = sb.toString();
			displayStringList.add(displayString);
		}
	}
	*/

	public void deleteEvent(int eventId) {
		database.delete(EventListHelper.EVENT_TABLE_NAME, 
				EventListHelper.EVENT_ID + " = " + eventId, null);
		
	}
	
	public SQLiteDatabase getDatabase() {
		return database;
	}

	public EventListHelper getElHelper() {
		return elHelper;
	}
}
