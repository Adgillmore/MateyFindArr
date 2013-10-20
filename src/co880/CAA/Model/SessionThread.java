package co880.CAA.Model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Adam
 * This class executes a thread to monitor the expiry
 * time of the event and keeps track of the time passed.
 */
public class SessionThread extends Thread implements Runnable{
	private long endTime;
	private Handler mainThreadHandler;
	
	public SessionThread(Handler handler, long inTime) {
		endTime = inTime;
		mainThreadHandler = handler;
	}

	/**
	 * Monitors the amount of time left and checks 
	 * whether the event has expired or not.
	 */
	public void run() {
		Log.i("SessionThread", "Timer started");
		while (endTime > System.currentTimeMillis()) {
			Log.i("SessionThread", "Event not expired");
			long difference = endTime - System.currentTimeMillis();
			Message msg = this.mainThreadHandler.obtainMessage(0);
			msg.setData(convertLongToBundle(difference));
			this.mainThreadHandler.sendMessage(msg);
			try{
				Log.i("SessionThread", "Thread sleeping");
				Thread.sleep(60000); //updates every minute
			} catch(InterruptedException x){
					throw new RuntimeException("interrupted",x);
			}
		}
		Log.i("SessionThread", "Event expired");
		Message m = this.mainThreadHandler.obtainMessage(13);
		m.setData(convertToBundle(false));
		this.mainThreadHandler.sendMessage(m);
	}
	
	private Bundle convertToBundle(boolean value) {
		Bundle b = new Bundle();
		b.putBoolean("eventActive", value);
		return b;
	}
	
	private Bundle convertLongToBundle (long l) {
		Bundle b = new Bundle();
		b.putLong("timeDifference", l);
		return b;
	}
}
