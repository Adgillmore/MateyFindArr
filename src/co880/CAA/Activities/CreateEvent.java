package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.Calendar;
import co880.CAA.R;
import co880.CAA.Model.CalendarModel;
import co880.CAA.Model.MyEventDb;
import co880.CAA.Model.RawContactManager;
import co880.CAA.Model.TimeConverter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dan, Ka & Adam - Activity which provides user input for creating
 * an event.
 */
public class CreateEvent extends AbstractEventActivity {

	TextView invitedFriendsTextView;

	Button defineBoundaryButton;
	Button selectFriendsButton;

	ArrayList<String> attending;

	private int latitude;
	private int longitude;
	private int radius;

	private Button helpButton;
	private AlertDialog helpDialog;

	@SuppressLint("WorldReadableFiles")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);

		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Molot.otf");
        TextView tv = (TextView) findViewById(R.id.createEventTitle);
        tv.setTypeface(tf);
        
		pref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
		calendarId = pref.getString("calendarId", null);

		// TimeConverter object has functions to covert times from epoch to
		// traditional time displays and vice versa
		timeConverter = new TimeConverter();

		instantiateUiElements();

		attending = new ArrayList<String>();
		latitude = 0;
		longitude = 0;
		radius = 0;

		// Build a dialog for assistance in using boundary functionality
		buildHelpDialog();
		
		setCurrentTimeOnViews();
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
		helpButton = (Button) findViewById(R.id.buttonHelp);
		defineBoundaryButton = (Button) findViewById(R.id.boundaryButton);
		submitEvent = (Button) findViewById(R.id.createEventButton);
		selectFriendsButton = (Button) findViewById(R.id.selectFriends);

		startDateTextView = (TextView) findViewById(R.id.startDate);
		startTimeTextView = (TextView) findViewById(R.id.startTime);
		endDateTextView = (TextView) findViewById(R.id.endDate);
		endTimeTextView = (TextView) findViewById(R.id.endTime);
		invitedFriendsTextView = (TextView) findViewById(R.id.invitedFriends);

		titleEditText = (EditText) findViewById(R.id.eventTitle);
		descriptionEditText = (EditText) findViewById(R.id.eventDesc);
		defineBoundaryButton.setOnClickListener(boundaryButtonListener);
		helpButton.setOnClickListener(helpButtonListener);
		submitEvent.setOnClickListener(createSubmitButtonListener);
		selectFriendsButton.setOnClickListener(selectFriendsButtonListener);

		startDateTextView.setOnClickListener(startDateClickListener);
		startTimeTextView.setOnClickListener(startTimeClickListener);
		endDateTextView.setOnClickListener(endDateClickListener);
		endTimeTextView.setOnClickListener(endTimeClickListener);

		invitedFriendsTextView.setOnClickListener(invitedFriendsListener);
	}
	
	/**
	 * @author Adam - method to create a dialog for assisting in the use of the
	 *         boundary functionality. Called when user presses '?' button
	 */
	public void buildHelpDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Event Boundary")
				.setMessage(
						"Set an optional boundary for your event. Location sharing will only"
								+ " occur within the boundary for the duration of the event. You will still "
								+ "see your own location if you are outside the boundary.")
				.setCancelable(true)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						helpDialog.cancel();
					}
				});
		helpDialog = builder.create();
	}

	/**
	 * @author Dan - method to create confirmation dialog when event is created
	 *         and all required details have been entered. Confirming this
	 *         dialog will insert event into the calendarModel, canceling will
	 *         dismiss the dialog and do nothing.
	 * @param mTitle - title of event
	 * @param mDescription - description of event
	 * @param mStartEpoch - start date/time in epoch
	 * @param mEndEpoch - end date/time in epoch
	 */
	public void createConfirmationDialog(final String mTitle,
			final String mDescription, final String mStartEpoch,
			final String mEndEpoch) {
		final CalendarModel cal = new CalendarModel(getBaseContext());
		cal.setOwnerEvent(true);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Event Created")
				.setMessage(
						"Date: " + mStartDate + " Start Time: " + mStartTime
								+ " End Time: " + mEndTime
								+ " Invited Attendees: " + attending.size())
				.setCancelable(true)
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								cal.insertNewEvent(calendarId, mTitle,
										mDescription, mStartEpoch, mEndEpoch,
										attending, radius, latitude, longitude);
								confirmDialog.cancel();
								CreateEvent.this.finish();
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
	 * @author Dan - onClickListener for boundaryButton
	 */
	View.OnClickListener boundaryButtonListener = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("co880.CAA",
					"co880.CAA.Activities.SetBoundaryActivity"));
			startActivityForResult(intent, 5);
		}
	};

	/**
	 * @author Dan - onClickListener for helpButton - shows boundary help dialog
	 */
	View.OnClickListener helpButtonListener = new View.OnClickListener() {
		public void onClick(View v) {
			helpDialog.show();
		}
	};
	
	/**
	 * @author Dan - onClickListener for InvitedFriends TextView
	 */
	View.OnClickListener invitedFriendsListener = new View.OnClickListener() {
		public void onClick(View v) {
			invitedFriendsDialog();
		}
	};

	/**
	 * @author Dan - Creates a dialog consisting of all friends currently 
	 * invited to the event, also allows user to clear the list and start again
	 */
	public void invitedFriendsDialog() {
		String[] items = new String[attending.size()];
		items = attending.toArray(items);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Invited Friends");
		if (attending.size() >= 1) {
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
				}
			});

			builder.setNegativeButton("Clear List",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							attending.clear();
							invitedFriendsTextView.setText("None Selected");
							dialog.dismiss();
						}
					});

			builder.setPositiveButton("Okay",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

		} else {
			builder.setMessage("Empty");
		}
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * @author Dan - onClickListener for selectFriends Button
	 */
	View.OnClickListener selectFriendsButtonListener = new View.OnClickListener() {
		public void onClick(View v) {
			createListDialog();
		}
	};

	/**
	 * @author Dan - method to create a list dialog consisting of the raw contacts
	 * that have been added to our app in FriendManager. Includes a link to FriendManager
	 * in order to add friends should this be needed
	 */
	public void createListDialog() {
		RawContactManager r = new RawContactManager();
		ArrayList<String> rawContacts = r.getRawContacts(getBaseContext());
		String[] contacts = new String[rawContacts.size()];
		contacts = rawContacts.toArray(contacts);
		final String[] mContacts = contacts;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Please select friends to include in your event");
		builder.setItems(contacts, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				String mItem = mContacts[item];
				if (attending.contains(mItem)) {
				} else {
					attending.add(mItem);
					invitedFriendsTextView.setText("" + attending.size()
							+ " People Attending");
				}
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("Friend Manager",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setComponent(new ComponentName("co880.CAA",
								"co880.CAA.Activities.FriendManagerActivity"));
						startActivity(intent);
					}
				});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * @author Dan - Get the current date and save to fields
	 */
	public void getDateAndTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		eYear = c.get(Calendar.YEAR);
		eMonth = c.get(Calendar.MONTH);
		eDay = c.get(Calendar.DAY_OF_MONTH);
		eHour = (mHour + 1);
		eMinute = (mMinute + 1);
	}
	
	/**
	 * @author Dan - Method to invoke getDateAndTime and the set the time
	 * and date on each TextView
	 */
	public void setCurrentTimeOnViews() {
		// display the current date and time
		getDateAndTime();
		updateDateDisplay(startDateTextView);
		updateDateDisplay(endDateTextView);
		updateTimeDisplay(startTimeTextView, false);
		updateTimeDisplay(endTimeTextView, true);
		
		assignViews();
	}
	
	/**
	 * @author Dan - Update the display for a date textView
	 * 
	 * @param TextView v - the TextView to update with date
	 */
	public void updateDateDisplay(TextView v) {
		v.setText(new StringBuilder()
			// Month is 0 so add 1
			.append(pad(mDay)).append("-").append(pad(mMonth + 1)).append("-")
			.append(mYear));
	}
	
	/**
	 * @author Dan - Method to set times on TextViews
	 * @param TextView v - TextView to set time on
	 * @param boolean b - True if time is for endTime when activity is
	 *  first created, so event by default will finish in 1 hours time
	 */
	public void updateTimeDisplay(TextView v, boolean b) {
		if (b) {
			if(mHour < 23) {
				v.setText(new StringBuilder()
					.append(pad(mHour + 1)).append(":").append(pad(mMinute)));
			} else {
				mHour = 00;
				v.setText(new StringBuilder()
					.append(pad(mHour)).append(":").append(pad(mMinute)));
			}
		} else {
			v.setText(new StringBuilder()
				.append(pad(mHour)).append(":").append(pad(mMinute)));
		}
	}
	
	/**
	 * @author Adam - return latitude, longitude and radius from SetBoundaryActivity
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Bundle boundaryData = data.getBundleExtra("boundaryData");
			latitude = boundaryData.getInt("latitude");
			longitude = boundaryData.getInt("longitude");
			radius = (int) boundaryData.getFloat("radius");
			Toast.makeText(this, "Radius: " + radius, Toast.LENGTH_SHORT)
					.show();
			// Store the boundary data
		}
	}

	public Button getSubmitEvent() {
		return submitEvent;
	}

	public void setCreateEvent(Button submitEvent) {
		this.submitEvent = submitEvent;
	}

	public Button getDefineBoundaryButton() {
		return defineBoundaryButton;
	}
}
