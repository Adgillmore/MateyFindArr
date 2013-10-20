package co880.CAA.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author Adam.
 * Class that creates the SQLite database, the _ID is linked to our google calendar entry. We store additional info such as boundary details.
 *
 */
public class EventListHelper extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "orpheus.db";
    public static final String EVENT_TABLE_NAME = "EventList";
    public static final String EVENT_ID = "eventId";
    public static final String COLUMN_ID = "_id";
    public static final String BOUNDARY_LATITUDE = "boundLat";
    public static final String BOUNDARY_LONGITUDE = "boundLong";
    public static final String BOUNDARY_RADIUS = "boundRad";
    private static final String EVENT_TABLE_CREATE =
                "CREATE TABLE " + EVENT_TABLE_NAME + 
                " (" + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + EVENT_ID + " INTEGER NOT NULL, " + BOUNDARY_RADIUS + " INTEGER NOT NULL, " + BOUNDARY_LATITUDE + " INTEGER NOT NULL, " + BOUNDARY_LONGITUDE + " INTEGER NOT NULL);";
    
	public EventListHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(EVENT_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
