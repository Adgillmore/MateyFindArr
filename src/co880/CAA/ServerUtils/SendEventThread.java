package co880.CAA.ServerUtils;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Handler;

/**
 * 
 * @author Adam
 * This class sends the event to the GCM servers for distribution to other users
 */
public class SendEventThread extends Thread implements Runnable {
	
	private Handler eventHandler;
	private JSONObject eventObject;
	private SendEventDetails sendEvent;
	private ArrayList<String> users;
	private InputStream result;
	
	public SendEventThread(Handler inHandler, JSONObject inObject, ArrayList<String> inUsers) {
		eventHandler = inHandler;
		eventObject = inObject;
		sendEvent = new SendEventDetails();
		users = inUsers;
		
	}
	
	/**
	 * @author Adam
	 * Sends the JSON object to the server and receives a success message from the GCM servers
	 */
	public void run() {
		String resultString = "";
		result = sendEvent.shareEvent(eventObject, users);
		if (result != null) {
			resultString = new ReadJSONStream().JsonToString(result);
		} else {
			resultString = "[{'success':'no result'}]";
		}
	}

}
