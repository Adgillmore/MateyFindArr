package co880.CAA.ServerUtils;

import java.io.InputStream;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class checkContactsThread extends Thread implements Runnable {

	// Get other users' locations
	// Inform parent Activity

	// Fields
	private Handler mainThreadHandler;
	private ArrayList<String> users;
	private InputStream contacts;
	private String myJSONString;
	private CrossCheckContacts crossCheck;
	private boolean queryServer;
	private int messagesSent;

	public static String tag = "WorkerThreadRunnable";

	// Constructor
	public checkContactsThread(Handler inHandler, ArrayList<String> inUsers) {
		mainThreadHandler = inHandler;
		users = inUsers;
		crossCheck = new CrossCheckContacts();
		queryServer = true;
		messagesSent = 0;
	}

	// Methods
	public void run() {
		showDialogMessage(0, "start");
		contacts = crossCheck.sendRequest(users);
		if (contacts != null) {
			myJSONString = new ReadJSONStream().JsonToString(contacts);
		} else {
			myJSONString = "[{'gmail':'Network error'}]";
		}

		// pass JSONString to the handler in a message as bundle for parsing
		Message m = this.mainThreadHandler.obtainMessage(42);
		m.setData(convertToBundle(myJSONString));
		this.mainThreadHandler.sendMessage(m);
		messagesSent++; // used by the JUnit test GetUsersLocThreadTest
		showDialogMessage(1, "done");
	}

	private void showDialogMessage(int id, String value) {
		Message m = this.mainThreadHandler.obtainMessage(id);
		m.setData(convertProgressToBundle(value));
		this.mainThreadHandler.sendMessage(m);
	}

	private Bundle convertToBundle(String value) {
		Bundle b = new Bundle();
		b.putString("checkedContacts", value);
		return b;
	}

	private Bundle convertProgressToBundle(String value) {
		Bundle b = new Bundle();
		b.putString("progress", value);
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
