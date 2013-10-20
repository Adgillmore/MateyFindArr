package co880.CAA.ServerUtils;

import java.io.InputStream;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * @author Adam
 * This class is a thread that runs the sendRequest method of
 * GetOtherUsers to retrieve other users' locations
 */
public class GetUsersLocThread extends Thread implements Runnable {
	
	//Fields
	private Handler mainThreadHandler;
	private Set<String> users;
	private InputStream locations;
	private String myJSONString;
	private GetOtherLocations getOthLoc;
	private boolean queryServer;
	private int messagesSent;
	private Context ctx;
	private int updateF;
	
	public static String TAG = "GetUsersLocThread";
	
	//Constructor
	public GetUsersLocThread(Handler inHandler, Set<String> inUsers, Context inCtx) {
		ctx = inCtx;
		mainThreadHandler = inHandler;
		users = inUsers;
		getOthLoc = new GetOtherLocations();
		queryServer = true;
		messagesSent = 0;
		getUpdateFreq();
		
	}
	
	/**
	 * Retrieves the user's preferred update frequency to use as the 
	 * basis for how often to request location updates.
	 */
	public void getUpdateFreq() {
		// Retrieve update frequency from shared preferences
		SharedPreferences updatePref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String updateFreq = (updatePref.getString("listPref", "60000"));
		updateF = Integer.valueOf(updateFreq);
	}
	

	//Methods
	public void run() {
		
		Log.d(TAG, "start execution");
		while (queryServer) {
		locations = getOthLoc.sendRequest(users);
			if (locations != null) {
				myJSONString = new ReadJSONStream().JsonToString(locations);
			}
			else {
			myJSONString = null;
			}
		
			//pass JSONString to the handler in a message as bundle for parsing
			Message m = this.mainThreadHandler.obtainMessage(42);
			m.setData(convertToBundle(myJSONString));
			this.mainThreadHandler.sendMessage(m);
			messagesSent++; //used by the JUnit test GetUsersLocThreadTest
		
			try{
				Thread.sleep(updateF);
			} catch(InterruptedException x){
					throw new RuntimeException("interrupted",x);
			}
		}
		
	}
		
	private Bundle convertToBundle(String value) {
		Bundle b = new Bundle();
		b.putString("locations", value);
		return b;
	}
	
	public boolean getQueryServer() {
		return queryServer;
	}


	public void setQueryServer(boolean queryServer) {
		this.queryServer = queryServer;
	}
	
	public int getMessageSent() {
		return messagesSent;
	}
	
	public String getMyJSONString() {
		return myJSONString;
	}
}
