package co880.CAA.ServerUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import co880.CAA.Activities.CAAActivity;

import com.google.android.gcm.GCMRegistrar;

/**
 * 
 * @author Adam and Ka
 * The body of this class was taken from the Android documentation 
 * on GCM (http://developer.android.com/guide/google/gcm/gs.html)
 * and we implemented this within a thread
 *
 */
public class RegisterThread extends Thread implements Runnable {

	private CAAActivity parentActivity;
	private Handler mainThreadHandler;
	private static final String SENDER_ID = "";//Your Google GCM Sender ID here
	private static String TAG;

	public RegisterThread(Handler handler, CAAActivity inActivity) {
		parentActivity = inActivity;
		mainThreadHandler = handler;

	}

	public void run() {

		GCMRegistrar.checkDevice(parentActivity);
		GCMRegistrar.checkManifest(parentActivity);
		final String regId = GCMRegistrar.getRegistrationId(parentActivity);

		if (regId.equals("")) {
			int counter = 0;
			while (!GCMRegistrar.isRegistered(parentActivity) && counter < 20) {
				GCMRegistrar.register(parentActivity, SENDER_ID);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			Log.v(TAG, "Already registered");
		}

		this.mainThreadHandler.sendEmptyMessage(7);
	}

}
