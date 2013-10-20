package co880.CAA.ServerUtils;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import co880.CAA.Activities.FriendManagerActivity;

/**
 * 
 * @author Ka lan.
 * This class processes the data from the server regarding the contacts.
 * Modified from Adam's Handler blueprint.
 *
 */
public class checkContactsHandler extends Handler {
	
	//Fields
	private FriendManagerActivity parentActivity;
	private String myJSONString;
	private JSONParser myParser;
	private ArrayList<String> contacts;
	private String[] keys;
	

	//Constructor
	public checkContactsHandler(FriendManagerActivity inActivity, String [] inKeys) {
		parentActivity = inActivity;
		myParser = new JSONParser();
		keys = inKeys;
	}
	
	//Methods
	@Override
	public void handleMessage(Message message) {
		if(message.what == 42) {
			handleJson(message);
		} else if(message.what == 0) {
			parentActivity.showProgressDialog();
		} else if(message.what == 1) {
			parentActivity.endProgressDialog();
		}
	}
	
	/**
	 * @author Ka lan
	 * Logic to deal with the data.
	 */
	public void handleJson(Message message) {
		myJSONString = retrieveStringFromMessage(message);
		String[] tempContacts = myParser.parse(myJSONString, keys, true);
		contacts = new ArrayList<String>(Arrays.asList(tempContacts));
		// Null is returned from server.
		if (contacts == null) {
			parentActivity.setQueryListViewReturned(true);
		}
		// No value is found from manual add set all booleans as done and return toast.
			else if ((parentActivity.isCreateRawContactRequest() == true && contacts.get(0) == "no value found")) {
			parentActivity.setQueryListViewReturned(true);
			parentActivity.setCreateRawContactRequest(false);
			Toast.makeText(parentActivity, "Gmail not registered on our server" , Toast.LENGTH_SHORT).show();
		}
		//If the create request returns a successful candidate then it is created and updated accordingly.
		else if ((contacts.size() >= 1) && (parentActivity.isCreateRawContactRequest() == true)) {
			parentActivity.setQueryListViewReturned(true);
			parentActivity.getRawContact().createBulkContacts(parentActivity, contacts);
			//This is to deal with findMyfriends request.
			if (parentActivity.getCheckedContacts().contains(contacts.get(0))) {
				String gmail = contacts.get(0);
				parentActivity.getCheckedContacts().remove(gmail);
			}
			parentActivity.setCreateRawContactRequest(false);
			parentActivity.updateListView();
			Toast.makeText(parentActivity, contacts.get(0) + " added", Toast.LENGTH_SHORT).show();
			
		}
		//Other queries more specific to the find friends rather than manual one.
		else if (contacts.contains("no value found")) {
			parentActivity.setQueryListViewReturned(true);
		}
		else {
			parentActivity.setCheckedContacts(contacts);
			parentActivity.setQueryListViewReturned(true);
			parentActivity.updateListView();
		}
	}
	
	public String retrieveStringFromMessage(Message message) {
		return message.getData().getString("checkedContacts");
	}
	
	public String getMyJSONString() {
		return myJSONString;
	}

	public void setMyJSONString(String myJSONString) {
		this.myJSONString = myJSONString;
	}
	
	
}
