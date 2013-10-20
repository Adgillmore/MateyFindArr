package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.MyEventDb;
import co880.CAA.Model.TimeConverter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 
 * @author Ka lan
 * Refactored duplication into this abstract class between CreateEvent and EditEvent.
 *
 */
public abstract class AbstractEventActivity extends Activity {

	protected  MyEventDb database;
	protected TimeConverter timeConverter;
	protected SharedPreferences pref;
	protected String calendarId;
	
	protected Button submitEvent;
	
	protected EditText titleEditText;
	protected EditText descriptionEditText;
	protected TextView startTimeTextView;
	protected TextView startDateTextView;
	protected TextView endTimeTextView;
	protected TextView endDateTextView;

	static final int START_DATE_DIALOG_ID = 0;
	static final int START_TIME_DIALOG_ID = 1;
	static final int END_DATE_DIALOG_ID = 2;
	static final int END_TIME_DIALOG_ID = 3;
	
	protected String mEndDate;
	protected String mStartDate;
	protected String mEndTime;
	protected String mStartTime;
	
	protected int mYear;
	protected int mMonth;
	protected int mDay;

	protected int mHour;
	protected int mMinute;
	
	protected int eYear;
	protected int eMonth;
	protected int eDay;

	protected int eHour;
	protected int eMinute;
	
	protected String TAG = "Create Event";
	protected AlertDialog confirmDialog;

	@SuppressLint("WorldReadableFiles")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		database = new MyEventDb(this);
		database.open();
		timeConverter = new TimeConverter();
		pref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
		calendarId = pref.getString("calendarId", null);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		database.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		database.open();
	}

	public void instantiateUiElements() {
		
	}
	/**
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean validNewEventTime(String start, String end) {
		ArrayList<Integer> eventIds = database.getEventsIds();
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
	 * @param newStart
	 * @param newEnd
	 * @param existingStart
	 * @param existingEnd
	 * @return
	 */
	public boolean compareTimes(Long newStart, Long newEnd, Long existingStart, Long existingEnd) {
		if(newStart < existingStart && newEnd < existingStart) {
			return true;
		} else if(newStart > existingStart && newStart > existingEnd) {
			return true;
		} else {
			return false;
		}
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
				} else if(!validNewEventTime(mStartEpoch, mEndEpoch)) {
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

	public void createConfirmationDialog(final String mTitle,
			final String mDescription, final String mStartEpoch,
			final String mEndEpoch) {
	}

	/**
	 * @author Dan - onClickListener for StartDate TextView - shows DatePicker dialog
	 */
	View.OnClickListener startDateClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showDialog(START_DATE_DIALOG_ID);
		}
	};

	/**
	 * @author Dan - onClickListener for EndDate TextView - shows DatePicker dialog
	 */
	View.OnClickListener endDateClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showDialog(END_DATE_DIALOG_ID);
		}
	};

	/**
	 * @author Dan - onClickListener for StartTime TextView - shows TimePicker dialog
	 */
	View.OnClickListener startTimeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showDialog(START_TIME_DIALOG_ID);
		}
	};

	/**
	 *@author Dan - onClickListener for EndTime TextView - shows TimePicker dialog
	 */
	View.OnClickListener endTimeClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			showDialog(END_TIME_DIALOG_ID);
		}
	};
	

	public void assignViews() {
		mStartDate = timeConverter.dateAmalgamation(mMonth + 1, mDay, mYear);
		mEndDate = timeConverter.dateAmalgamation(eMonth +1, eDay, eYear);
		mStartTime = timeConverter.timeAmalgamation(pad(mHour), pad(mMinute));
		mEndTime = timeConverter.timeAmalgamation(pad(eHour), pad(eMinute));
	}
	
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public void updateDateDisplay(TextView v, String dateOrTime) {
		if (dateOrTime.equals("start")) {
		v.setText(new StringBuilder()
			// Month is 0 so add 1
			.append(pad(mDay)).append("-").append(pad(mMonth + 1)).append("-")
			.append(mYear));
		}
		else {
			v.setText(new StringBuilder()
			// Month is 0 so add 1
			.append(pad(eDay)).append("-").append(pad(eMonth + 1)).append("-")
			.append(eYear));
		}
	}
	
	public void updateTimeDisplay(TextView v, boolean b, String dateOrTime) {
		if (dateOrTime.equals("start")) {
		if (b) {
			v.setText(new StringBuilder()
			.append(pad(mHour)).append(":").append(pad(mMinute)));
		} else {
			v.setText(new StringBuilder()
				.append(pad(mHour)).append(":").append(pad(mMinute)));
		}
		} 
		else {
			if (b) {
				v.setText(new StringBuilder()
				.append(pad(eHour)).append(":").append(pad(eMinute)));
			} else {
				v.setText(new StringBuilder()
					.append(pad(eHour)).append(":").append(pad(eMinute)));
			}
		}
	}

	/**
	 * @author Dan - The DatePicker for the start date of a timed event
	 */
	protected DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			mStartDate = timeConverter.dateAmalgamation(mMonth +1,
					mDay, mYear);
			updateDateDisplay(startDateTextView, "start");
		}
	};

	/**
	 * @author Dan - The DatePicker for the end date of a timed event
	 */
	protected DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			eYear = year;
			eMonth = monthOfYear;
			eDay = dayOfMonth;
			mEndDate = timeConverter.dateAmalgamation(eMonth +1,
					eDay, eYear);
			updateDateDisplay(endDateTextView, "end");
		}
	};

	/**
	 * @author Dan - The TimePicker for the start of a time event
	 */
	protected TimePickerDialog.OnTimeSetListener mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			mStartTime = timeConverter.timeAmalgamation(pad(mHour), pad(mMinute));
			updateTimeDisplay(startTimeTextView, false, "start");
		}
	};

	/**
	 * @author Dan - The TimePicker for the end of a timed event
	 */
	protected TimePickerDialog.OnTimeSetListener mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			eHour = hourOfDay;
			eMinute = minute;
			mEndTime = timeConverter.timeAmalgamation(pad(eHour), pad(eMinute));
			updateTimeDisplay(endTimeTextView, false, "end");
		}
	};

	/**
	 * 
	 * @author Dan - Override onCreateDialog to create dialog based on which onClickListener
	 * is used
	 * 
	 * @param int id - of dialog to create
	 */
	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mStartDateSetListener, mYear,
					mMonth, mDay);
		case START_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mStartTimeSetListener, mHour,
					mMinute, true);
		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mEndDateSetListener, eYear,
					eMonth, eDay);
		case END_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mEndTimeSetListener, eHour,
					eMinute, true);
		}
		return null;
	}
	
	public int getmMonth() {
		return mMonth;
	}

	public int geteDay() {
		return eDay;
	}

	public MyEventDb getDatabase() {
		return database;
	}

	public String getCalendarId() {
		return calendarId;
	}
}
