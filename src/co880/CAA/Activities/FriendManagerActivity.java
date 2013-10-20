package co880.CAA.Activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import co880.CAA.R;
import co880.CAA.Model.ContactsModel;
import co880.CAA.Model.RawContactManager;
import co880.CAA.ServerUtils.EmailIntentManager;
import co880.CAA.ServerUtils.checkContactsHandler;
import co880.CAA.ServerUtils.checkContactsThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Ka Lan This class is designed to manager all manners of the Friend
 *         components.
 */
public class FriendManagerActivity extends Activity {

	private Button findFr;
	private Button manAdd;
	private ListView lsView;
	private ArrayAdapter<String> appContactarrayAdapter;
	private RawContactManager rawContact;
	private ArrayList<String> checkedContacts;
	private boolean queryListViewReturned;
	private boolean createRawContactRequest;
	private Handler checkUsersHandler;
	private checkContactsThread checkConThrd;
	

	private ContactsModel conMod;
	private ProgressDialog progress;
	private SharedPreferences sharedPref;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_manager);

		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Molot.otf");
        TextView tv = (TextView) findViewById(R.id.friendManagerTitle);
        tv.setTypeface(tf);
		
		sharedPref = getSharedPreferences("caaPref", MODE_WORLD_READABLE);
		// Instantiate handler and thread.
		checkUsersHandler = null;
		checkConThrd = null;
		checkedContacts = new ArrayList<String>();

		conMod = new ContactsModel(this);
		setRawContact(new RawContactManager());

		// Compares local gmails with Raw Contacts of our type and compares with
		// the server.
		checkLocalAndCrossCheckWithServer(conMod.returnLocalGmails());

		findFr = (Button) findViewById(R.id.findFriend);
		manAdd = (Button) findViewById(R.id.manual);
		lsView = (ListView) findViewById(R.id.appContacts);

		// Update the listView to display relevant info.
		updateListView();

		// Button to find friends Raw Contacts.
		findFr.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Initially false and false at the start of the query when a
				// result is returned from the worker thread it is true.
				if (!queryListViewReturned) {
					Toast.makeText(
							getBaseContext(),
							"Retrieving Friends, please try again in a moment.",
							Toast.LENGTH_SHORT).show();
				} else if (checkedContacts.size() == 0) {
					Toast.makeText(getBaseContext(), "No Friends to Add",
							Toast.LENGTH_SHORT).show();
				}
				// If there are friends to add then a dialog box can be
				// created.
				else {
					createFindMyFriendsAlert(checkedContacts);
				}

			}
		});
		// For inputing Gmails manually which should match the registered users
		// on the server.
		manAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				createAddfriendManually();
			}
		});
	}

	public void showProgressDialog() {
		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setMessage("Synchronising Contacts");
		progress.show();
	}

	public void endProgressDialog() {
		progress.dismiss();
	}

	/**
	 * @param checkLocalGmails
	 *            is a list of gmails which we compare to the server with,
	 *            through a thread.
	 */
	public void getCheckUsers(ArrayList<String> gmails) {
		if (checkUsersHandler == null) {
			checkUsersHandler = new checkContactsHandler(this,
					new String[] { "gmail" });
			checkConThrd = new checkContactsThread(checkUsersHandler, gmails);
			checkConThrd.start();
			return;
		}

		if (checkConThrd.getState() != Thread.State.TERMINATED) {
			// Thread is already there
		} else {
			// Create new thread
			checkConThrd = new checkContactsThread(checkUsersHandler, gmails);
			checkConThrd.start();
		}

	}

	/**
	 * 
	 * @param c
	 * @param list
	 *            Sets an arrayAdapter to the lsView created onCreate. The
	 *            arrayAdapter will display the specified list of type
	 *            list_item_1. The OnItemClickListener launches an dialog which
	 *            you can edit the selected contact.
	 */
	public void createAppContactArrayAdapter(Context c, ArrayList<String> list) {
		appContactarrayAdapter = new ArrayAdapter<String>(c,
				android.R.layout.simple_expandable_list_item_1, list);
		lsView.setAdapter(appContactarrayAdapter);
		lsView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String j = (String) lsView.getItemAtPosition(arg2);
				createEditFriendsAlert(j);
			}

		});
	}

	// Creates a dialog to input manually emails.
	public void createAddfriendManually() {
		LayoutInflater li = LayoutInflater.from(getBaseContext());
		// Sets up the editTextView.
		View editTextView = li.inflate(R.layout.edit_text_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Add Friend Manually");
		alertDialogBuilder.setView(editTextView);

		final EditText userInput = (EditText) editTextView
				.findViewById(R.id.editTextDialogUserInput);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Submit",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String inputEmail = userInput.getText()
										.toString().toLowerCase();
								ArrayList<String> inputEmailArrayList = new ArrayList<String>();
								inputEmailArrayList.add(inputEmail);
								// Checks to see if the contact has already been
								// added as an raw contact.
								if (rawContact.checkAndReturnContacts(
										getBaseContext(), inputEmailArrayList)
										.size() == 0) {
									Toast.makeText(
											getBaseContext(),
											"You have already added "
													+ inputEmail,
											Toast.LENGTH_SHORT).show();
								} else if (inputEmail.equals(sharedPref
										.getString("email", null))) {
									Toast.makeText(
											getBaseContext(),
											"That's your own gmail you plonker! ",
											Toast.LENGTH_SHORT).show();
								}
								// We send a request to the server to create an
								// raw contact if the user is registered.
								else {
									HashSet<String> set = new HashSet<String>();
									set.add(inputEmail);
									createRawContactRequest = true;
									checkLocalAndCrossCheckWithServer(set);
								}
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	// Dialog box to edit friends
	public void createEditFriendsAlert(String name) {
		final String finalName = name;
		CharSequence[] editFriend = getBaseContext().getResources()
				.getStringArray(R.array.editFriend);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(finalName).setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		// Onclick items to act accordingly to order, array defined is
		// resources.
		alertDialogBuilder.setItems(editFriend,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 1) {
							getRawContact().deleteRawContact(getBaseContext(),
									finalName);

						} else if (item == 0){
							EmailIntentManager.launchEmailIntent(getBaseContext(), finalName);
						}
						if (conMod.returnLocalGmails().contains(finalName)) {
							checkedContacts.add(finalName);
						}
						updateListView();
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * @param list
	 *            The list is the list of friends, this method creates a dialog
	 *            to add friends.
	 */
	public void createFindMyFriendsAlert(ArrayList<String> list) {
		// Converts the list into CharSequence[] to be used in the dialog.
		final CharSequence[] friends = list.toArray(new CharSequence[list
				.size()]);
		// Boolean array to be linked with the friends array.
		final boolean[] booleanFriends = new boolean[friends.length];
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMultiChoiceItems(friends, booleanFriends,
				new DialogInterface.OnMultiChoiceClickListener() {
					// OnClick to check boxes depending on which is clicked.
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							booleanFriends[which] = true;
						} else {
							booleanFriends[which] = false;
						}
					}
				});
		alertDialogBuilder
				.setTitle("Select friends to add")
				.setPositiveButton("Submit",
						new DialogInterface.OnClickListener() {
							// Submit and retrieve gmails selected and create
							// raw contacts accordingly and update view.
							public void onClick(DialogInterface dialog, int id) {
								ArrayList<String> selectedGmails = getSelections(
										booleanFriends, friends);
								getRawContact().createBulkContacts(
										getBaseContext(), selectedGmails);
								HashSet<String> selectedGmailsHash = convertArrayListToHashSet(selectedGmails);
								checkLocalAndCrossCheckWithServer(selectedGmailsHash);
								updateListView();
								dialog.cancel();
							}
						})
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getListView().setItemChecked(
				alertDialog.getListView().getSelectedItemPosition(), true);
		alertDialog.show();
	}

	public void setCheckedContacts(ArrayList<String> contacts) {
		checkedContacts = contacts;
	}

	public void setQueryListViewReturned(boolean b) {
		queryListViewReturned = b;
	}
	
	public boolean getQueryListViewReturned() {
		return queryListViewReturned;
	}

	public ArrayList<String> getCheckedContacts() {
		return checkedContacts;
	}

	public ArrayList<String> convertHashSetToArrayList(HashSet<String> hashSet) {
		ArrayList<String> nArrayList = new ArrayList<String>();
		Iterator<String> itr = hashSet.iterator();
		while (itr.hasNext()) {
			nArrayList.add(itr.next());
		}
		return nArrayList;
	}

	public HashSet<String> convertArrayListToHashSet(ArrayList<String> list) {
		HashSet<String> nHashSet = new HashSet<String>();
		Iterator<String> itr = list.iterator();
		while (itr.hasNext()) {
			nHashSet.add(itr.next());
		}
		return nHashSet;
	}

	/**
	 * @param email
	 *            This method checks email param and then checks if they are Raw
	 *            before sending the full filtered list to compare with the
	 *            server.
	 */
	public void checkLocalAndCrossCheckWithServer(HashSet<String> email) {
		ArrayList<String> ArrayListLocalGmails = convertHashSetToArrayList(email);
		ArrayList<String> checkedLocalGmails = getRawContact()
				.checkAndReturnContacts(this, ArrayListLocalGmails);
		if (checkedLocalGmails.size() != 0) {
			queryListViewReturned = false;
			getCheckUsers(checkedLocalGmails);
		} else {
			Iterator<String> itr = email.iterator();
			while (itr.hasNext()) {
				checkedContacts.remove(itr.next());
			}
			if (createRawContactRequest == true) {
				createRawContactRequest = false;
			}
		}
	}

	/**
	 * 
	 * @param booleanList
	 *            a list of booleans to correspond with the friends CharSequence
	 *            array.
	 * @param friends
	 *            the friend is an ArrayList which is the selected friends.
	 * @return
	 */
	public ArrayList<String> getSelections(boolean[] booleanList,
			CharSequence[] friends) {
		ArrayList<String> selectedGmails = new ArrayList<String>();
		int count = booleanList.length;

		for (int i = 0; i < count; i++) {
			if (booleanList[i] == true) {
				selectedGmails.add(friends[i].toString());
			} else {

			}
		}
		return selectedGmails;
	}

	/**
	 * This updates the listView by getting the Raw Contacts list and setting
	 * the adapter to the listView.
	 */
	public void updateListView() {
		ArrayList<String> rawContacts = getRawContact().getRawContacts(
				getBaseContext());
		createAppContactArrayAdapter(getBaseContext(), rawContacts);
	}

	public RawContactManager getRawContact() {
		return rawContact;
	}

	public void setRawContact(RawContactManager rawContact) {
		this.rawContact = rawContact;
	}

	public boolean isCreateRawContactRequest() {
		return createRawContactRequest;
	}

	public void setCreateRawContactRequest(boolean createRawContactRequest) {
		this.createRawContactRequest = createRawContactRequest;
	}

	public Handler getCheckUsersHandler() {
		return checkUsersHandler;
	}

	public checkContactsThread getCheckConThrd() {
		return checkConThrd;
	}

}