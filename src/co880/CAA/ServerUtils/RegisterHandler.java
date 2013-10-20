package co880.CAA.ServerUtils;

import com.google.android.gcm.GCMRegistrar;

import co880.CAA.Activities.CAAActivity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Adam
 * A simple class to log a message that GCM registration was successful
 */
public class RegisterHandler extends Handler {
	
	private boolean handledMessage;
	private CAAActivity parentActivity;
	public final static String TAG = "RegisterHandler";
	
	
	public RegisterHandler (CAAActivity inActivity) {
		parentActivity = inActivity;
		handledMessage = false;
	}
	
	public void handleMessage(Message message) {
		Log.i(TAG, "GCM registration successful");
		handledMessage = true;
	}

	public boolean isHandledMessage() {
		return handledMessage;
	}

	public void setHandledMessage(boolean handledMessage) {
		this.handledMessage = handledMessage;
	}
	
	

}
