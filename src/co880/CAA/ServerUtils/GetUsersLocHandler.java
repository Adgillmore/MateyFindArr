package co880.CAA.ServerUtils;

import android.os.Handler;
import android.os.Message;
import co880.CAA.Activities.LocationActivity;

/**
 * 
 * @author Adam
 * This class handles messages from the GetUsersLocThread which 
 * contain data on other users locations, and calls methods to 
 * process that data.
 */
public class GetUsersLocHandler extends Handler {
	
	//Fields
	public static final String tag = "GetLocationHandler";
	private LocationActivity parentActivity;
	private String myJSONString;
	private JSONParser myParser;
	private String[] keys;
	private String[][] locations;
	private boolean receivedMessage;

	//Constructor
	public GetUsersLocHandler(LocationActivity inActivity, String[] inKeys) {
		parentActivity = inActivity;
		myParser = new JSONParser();
		keys = inKeys;
		receivedMessage = false;
	}
	
	//Methods
	@Override
	public void handleMessage(Message message) {
		receivedMessage = true; //used for JUnit testing
		myJSONString = retrieveStringFromMessage(message);
		locations = myParser.twoDParse(myJSONString, keys);
		parentActivity.setOtherUsersLocs(locations);
		parentActivity.drawOthers();
		parentActivity.getMapView().invalidate(); //forces refresh of the map
		parentActivity.buildUsersDialog();
		parentActivity.activeUsers(); //updates the active users display
	}
	
	public String retrieveStringFromMessage(Message message) {
		return message.getData().getString("locations");
	}
	
	public String getMyJSONString() {
		return myJSONString;
	}

	public void setMyJSONString(String myJSONString) {
		this.myJSONString = myJSONString;
	}

	public boolean isReceivedMessage() {
		return receivedMessage;
	}

	public void setReceivedMessage(boolean receivedMessage) {
		this.receivedMessage = receivedMessage;
	}
}
