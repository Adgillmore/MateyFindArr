package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import co880.CAA.R;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.MyEventDb;
import co880.CAA.ServerUtils.EmailIntentManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Ka lan 
 * This class displays the upcoming events and allows you to edit them accordingly.
 */

public class MyEvents extends Activity {

	private Button submit;
	private ListView results;
	private MyEventDb database;
	private MyEvents parentActivity;
	private ArrayList<Integer> eventIds;
	private CalendarModel myCalendar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_events);

		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Molot.otf");
        TextView tv = (TextView) findViewById(R.id.myEventsTitle);
        tv.setTypeface(tf);
		
		parentActivity = this;
		
		results = (ListView) findViewById(R.id.queryResult);
		submit = (Button) findViewById(R.id.queryButton1);

		myCalendar = new CalendarModel(this);
		database = new MyEventDb(this);
		database.open();

		submit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				updateListview();
				if (eventIds.isEmpty()) {
					Toast.makeText(parentActivity, "There are no Upcoming Events", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	protected void onResume() {
		super.onResume();
		database.open();
		updateListview();
	}

	protected void onPause() {
		super.onPause();
		database.close();
	}

	/**
	 * @author Ka lan
	 * @param eventId
	 *            takes in the event id of the selected event needed so we can
	 *            refer to the same event.
	 *            Creates an edit event dialog with a couple of options.
	 */
	public void createEditEventsAlert(int eventId) {
		final int finaleventId = eventId;
		final HashMap<String, String> details = myCalendar
				.getEventDetails(finaleventId);
		CharSequence[] editEvent = getBaseContext().getResources()
				.getStringArray(R.array.editEvent);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(details.get("title")).setNegativeButton(
				"Back", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alertDialogBuilder.setItems(editEvent,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						//description
						if (item == 0) {
							createDescriptionAlert(details, finaleventId);
							// Invited Attendees
						} else if (item == 1) {
							createdAttendeesAlert(finaleventId);
							// Edit Event
						}else if (item == 2) {
							Intent intent = new Intent();
							intent.setComponent(new ComponentName("co880.CAA",
									"co880.CAA.Activities.EditEvent"));
							intent.putExtra("eventId", finaleventId);
							startActivity(intent);
							// Delete Event
						} else if (item == 3) {
							database.deleteEvent(finaleventId);
							Uri event = Uri
									.parse("content://com.android.calendar/events/"
											+ finaleventId);
							myCalendar.deleteEvent(event);
							updateListview();
							Toast.makeText(
									parentActivity,
									"Event: " + details.get("title")
											+ " deleted", Toast.LENGTH_SHORT)
									.show();
							dialog.cancel();
						}
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * @author Ka lan
	 * @param eventId 
	 * this alert brings up the list of attendees linked to the eventId.
	 */
	public void createdAttendeesAlert(final int eventId) {
		HashMap<String, String> attendees = CalendarModel.getAttendees(
				parentActivity, eventId);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Invited Attendees").setNegativeButton(
				"Back", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						createEditEventsAlert(eventId);
					}
				});
		if (attendees != null) {
			Set<String> gmails = attendees.keySet();
			SharedPreferences prefs = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
			String yourEmail = prefs.getString("email", null);
			gmails.remove(yourEmail);
			final CharSequence[] gmailsChar = gmails.toArray(new CharSequence[gmails
					.size()]);
		alertDialogBuilder.setItems(gmailsChar,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int item) {
						EmailIntentManager.launchEmailIntent(getBaseContext(), ""+ gmailsChar[item]);
					}
					});
				};
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * @author Ka lan
	 * @param inDetails
	 * @param eventId
	 * This alert brings up the details of the event.
	 */
	public void createDescriptionAlert(HashMap<String, String> inDetails,
			final int eventId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Description").setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						createEditEventsAlert(eventId);
					}
				});
		alertDialogBuilder.setMessage(inDetails.get("description"));
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	/**
	 * @Ka lan
	 * @param eventIds from the local database.
	 * @return eventIds that are not out of date or deleted.
	 */
	public ArrayList<Integer> removeOldEventIds(ArrayList<Integer> eventIds) {
		Iterator<Integer> itr = eventIds.iterator();
		while (itr.hasNext()){
			int temp = itr.next();
			if (!myCalendar.validEventId(temp)) {
				database.deleteEvent(temp);
				itr.remove();
			}
			else {
				// added a minute after end time because the session ended is checked every 30 seconds.
				HashMap<String, String> details = myCalendar.getEventDetails(temp);
				if (Long.parseLong(details.get("dtend"))+60000 < System.currentTimeMillis()) {
					database.deleteEvent(temp);
					itr.remove();
				}
			}
		}
		return eventIds;
	}

	/**
	 * @author Ka lan
	 * Calls removeOldEventsIds and sets the listView accordingly via createAppEventsArrayAdapter.
	 */
	public void updateListview() {
		ArrayList<Integer> tempEventIds = database.getEventsIds();
		if (tempEventIds != null) {
		eventIds = removeOldEventIds(tempEventIds);
		ArrayList<String> eventDetails = getAllEventsDetails(eventIds);
		createAppEventsArrayAdapter(eventDetails);
		}
		else {
			Log.i("MyEvents", "EventIds are null");
		}
	}

	/**
	 * @author Ka lan
	 * @param eventDetails 
	 * sets the adapter to the listView.
	 */
	public void createAppEventsArrayAdapter(final ArrayList<String> eventDetails) {
		ArrayAdapter<String> appResultarrayAdapter = new ArrayAdapter<String>(
				getparentActivity(), R.layout.event_details_list, eventDetails);
		results.setAdapter(appResultarrayAdapter);
		if (eventDetails.size() == 0) {
			Log.i("MyEvents", "there are no events");
		} else {
			results.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					createEditEventsAlert(eventIds.get(arg2));

				}
			});
		}
	}

	/**
	 * @author Ka lan
	 * @param list is the eventIds.
	 * @return eventDetails in the format of a String to populate the listView.
	 */
	public ArrayList<String> getAllEventsDetails(ArrayList<Integer> list) {
		ArrayList<String> eventDetails = myCalendar.getEventDetailsList(list);
		return eventDetails;
	}

	public MyEventDb getDatabase() {
		return database;
	}

	public Context getparentActivity() {
		return parentActivity;
	}
	
	public CalendarModel getMyCalendar() {
		return myCalendar;
	}

}
