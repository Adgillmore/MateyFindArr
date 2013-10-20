/**
 * 
 */
package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co880.CAA.R;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.TimeConverter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Ka lan
 * Made this class by modifying Dan's createEvent, it edits the specifed event which is launched by MyEvents.
 * 
 */
public class EditEvent extends AbstractEventActivity {

	private static String TAG = "Create Event";

	private int eventId;
	private String email;

	@Override
	@SuppressLint("WorldReadableFiles")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent() != null) {
			eventId = getIntent().getIntExtra("eventId", 0);
			if (eventId == 0) {
				Log.e("EditEvent", "Error no EventID");
			}
		}
		
		email = pref.getString("email", null);

		setContentView(R.layout.edit_event);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Molot.otf");
		TextView tv = (TextView) findViewById(R.id.editEventTitle);
		tv.setTypeface(tf);

		// TimeConverter object has functions to covert times from epoch to
		// traditional time displays and vice versa
		timeConverter = new TimeConverter();

		instantiateUiElements();

		CalendarModel cal = new CalendarModel(this);

		HashMap<String, String> details = cal.getEventDetails(eventId);
		titleEditText.setText(details.get("title"));
		descriptionEditText.setText(details.get("description"));
		Long startEpoch = Long.parseLong(details.get("dtstart"));
		Long endEpoch = Long.parseLong(details.get("dtend"));

		String startDateTime = TimeConverter
				.convertEpochTimeToDateTime(startEpoch);
		String endDateTime = TimeConverter.convertEpochTimeToDateTime(endEpoch);

		String start = "start";
		String end = "end";

		setDateAndTime(startDateTime, start);
		setDateAndTime(endDateTime, end);

		updateDateDisplay(startDateTextView, start);
		updateDateDisplay(endDateTextView, end);
		updateTimeDisplay(startTimeTextView, false, start);
		updateTimeDisplay(endTimeTextView, false, end);

		assignViews();
	}

	public void setDateAndTime(String dateTime, String startOrEnd) {
		if (startOrEnd.equals("start")) {
			mYear = Integer.parseInt(dateTime.substring(6, 10));
			mMonth = Integer.parseInt(dateTime.substring(0, 2)) - 1;
			mDay = Integer.parseInt(dateTime.substring(3, 5));
			mHour = Integer.parseInt(dateTime.substring(11, 13));
			mMinute = Integer.parseInt(dateTime.substring(14, 16));
		} else {
			eYear = Integer.parseInt(dateTime.substring(6, 10));
			eMonth = Integer.parseInt(dateTime.substring(0, 2)) - 1;
			eDay = Integer.parseInt(dateTime.substring(3, 5));
			eHour = Integer.parseInt(dateTime.substring(11, 13));
			eMinute = Integer.parseInt(dateTime.substring(14, 16));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void instantiateUiElements() {
		submitEvent = (Button) findViewById(R.id.editEventButton);

		startDateTextView = (TextView) findViewById(R.id.startDate);
		startTimeTextView = (TextView) findViewById(R.id.startTime);
		endDateTextView = (TextView) findViewById(R.id.endDate);
		endTimeTextView = (TextView) findViewById(R.id.endTime);

		titleEditText = (EditText) findViewById(R.id.eventTitle);
		descriptionEditText = (EditText) findViewById(R.id.eventDesc);
		submitEvent.setOnClickListener(createSubmitButtonListener);

		startDateTextView.setOnClickListener(startDateClickListener);
		startTimeTextView.setOnClickListener(startTimeClickListener);
		endDateTextView.setOnClickListener(endDateClickListener);
		endTimeTextView.setOnClickListener(endTimeClickListener);

	}

	public void createConfirmationDialog(final String mTitle,
			final String mDescription, final String mStartEpoch,
			final String mEndEpoch) {
		final CalendarModel cal = new CalendarModel(getBaseContext());
		cal.setOwnerEvent(true);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Confirm Changes")
				.setMessage(
						"Date: " + mStartDate + " Start Time: " + mStartTime
								+ " End Time: " + mEndTime)
				.setCancelable(true)
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								cal.updateMethod(calendarId, eventId, mTitle,
										mDescription, mStartEpoch, mEndEpoch, email);
								Toast.makeText(getBaseContext(),
										mTitle + " :updated.",
										Toast.LENGTH_SHORT).show();
								EditEvent.this.finish();
							}
						});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						confirmDialog.cancel();
					}
				});
		confirmDialog = builder.create();
		confirmDialog.show();
	}
	
	/**
	 * @author Dan and Ka lan.
	 * This method gets the relevant eventIds and details and compares them with your specified event and new times.
	 */
	@SuppressWarnings("rawtypes")
	public boolean validNewEventTime(String start, String end, int eventId) {
		ArrayList<Integer> eventIds = database.getEventsIds();
		Iterator<Integer> itr = eventIds.iterator();
		while(itr.hasNext()) {
			int tempEventId = itr.next();
			if (tempEventId == eventId) {
				itr.remove();
			}
		}
		boolean b = true;
		CalendarModel cm = new CalendarModel(this);
		HashMap<Integer, Long[]> eventTimes = cm.getEventTimes(eventIds);
		Long newStart = Long.parseLong(start);
		Long newEnd = Long.parseLong(end);
		Iterator it = eventTimes.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Long[] timeValues = (Long[]) pairs.getValue();
			Long existingStart = timeValues[0];
			Long existingEnd = timeValues[1];
			b = compareTimes(newStart, newEnd, existingStart, existingEnd);
			if(!b) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @author Dan - OnClickListener for create event button - sends details to be
	 *         inserted into calendar to CalendarModel object for processing
	 */
	View.OnClickListener createSubmitButtonListener = new View.OnClickListener() {
		public void onClick(View v) {
			String test = titleEditText.getText().toString();
			String test2 = descriptionEditText.getText().toString();
			// Make sure there is a title and a description
			if (test.equals("") || test2.equals("")) {
				Toast.makeText(getBaseContext(),
						"Please enter a Title and Description",
						Toast.LENGTH_LONG).show();
			} else {
				// Get the event details entered by the user
				String mTitle = titleEditText.getText().toString();
				String mDescription = descriptionEditText.getText().toString();

				StringBuilder fullStartTime = (new StringBuilder().append(
						mStartDate).append(" ").append(mStartTime));
				StringBuilder fullEndTime = (new StringBuilder().append(
						mEndDate).append(" ").append(mEndTime));
				String mEndEpoch = timeConverter.parseDateAndTime(fullEndTime
						.toString());
				String mStartEpoch = timeConverter
						.parseDateAndTime(fullStartTime.toString());
				final Long epochTime = System.currentTimeMillis();

				// Make sure event is in the future
				if (Long.valueOf(mStartEpoch) <= epochTime
						|| Long.valueOf(mEndEpoch) <= epochTime) {
					Toast.makeText(
							getBaseContext(),
							"The event is in the past, please set a future event",
							Toast.LENGTH_LONG).show();
					return;
				}
				// Also check that event doesn't end before it starts
				else if (Long.valueOf(mEndEpoch) <= Long.valueOf(mStartEpoch)) {
					Toast.makeText(getBaseContext(),
							"The event ends before it begins! Sort it out...",
							Toast.LENGTH_LONG).show();
					return;
				} else if(!validNewEventTime(mStartEpoch, mEndEpoch, eventId)) {
					Toast.makeText(getBaseContext(), "Conflicting Events! DANGER!!!", Toast.LENGTH_LONG).show();
					return;
				}
				if (calendarId != null) {
					// Create the confirmation dialog
					createConfirmationDialog(mTitle, mDescription, mStartEpoch,
							mEndEpoch);
				} else {
					Log.e(TAG, "Calendar ID is null");
				}
			}
		}
	};
	

}
