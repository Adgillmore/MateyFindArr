package co880.CAA.test;

import java.lang.Thread.State;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.CAAActivity;
import co880.CAA.ServerUtils.RegisterHandler;
import co880.CAA.ServerUtils.RegisterThread;
import junit.framework.TestCase;

public class RegisterHandlerAndThreadTest extends ActivityInstrumentationTestCase2<CAAActivity> {

	private CAAActivity parentActivity;
	private RegisterHandler mainThreadHandler;
	private RegisterThread regThread;
	private SharedPreferences pref;
	
	public RegisterHandlerAndThreadTest() {
		super(CAAActivity.class);
	}
	
	protected void setUp() throws Exception {
		parentActivity = getActivity();
		//mainThreadHandler = (RegisterHandler) parentActivity.getRegisterHandler();
		//regThread = parentActivity.getRegisterThread();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testRegisterHandler() {
		//Handler should have been instantiated during CAAActivity onCreate
		parentActivity.registerWithGCM();
		mainThreadHandler = (RegisterHandler) parentActivity.getRegisterHandler();
		assertNotNull(mainThreadHandler);
	}
	/*
	public void testRegisterThread() {
		//Thread will be initiated and will run but then terminate
		parentActivity.registerWithGCM();
		mainThreadHandler = (RegisterHandler) parentActivity.getRegisterHandler();
		regThread = parentActivity.getRegisterThread();
		//assertTrue(regThread.getState() == State.TERMINATED);
		mainThreadHandler.setHandledMessage(false);
		parentActivity.registerWithGCM();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(mainThreadHandler.isHandledMessage());
		//assertTrue(regThread.getState() == State.TERMINATED);
	}
	*/
	

	public void testButtonState() {
		//Buttons in main menu should have been activated during onCreate
		try {
			runTestOnUiThread(new Runnable() {
			     public void run() {
			    	 parentActivity.setButtons(false); //Simulate not being registered
			    }
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		assertFalse(parentActivity.getCe().isEnabled());
		assertFalse(parentActivity.getCre().isEnabled());
		assertFalse(parentActivity.getMe().isEnabled());		
	}
	
	
	public void testButtonState2() {
		//Buttons in main menu should have been activated during onCreate
 		parentActivity.registerWithGCM();
 		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			runTestOnUiThread(new Runnable() {
			     public void run() {

			    	parentActivity.setButtons((GCMRegistrar.isRegistered(parentActivity))); //Simulate not being registered
			    }
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
			
		assertFalse(parentActivity.getCe().isEnabled());
		assertTrue(parentActivity.getCre().isEnabled());
		assertTrue(parentActivity.getMe().isEnabled());
	}
	/*
	public void testRegistrationCheck() {
		//Retrieve regID from GCM sharedPreferences
		String originalRegId = GCMRegistrar.getRegistrationId(parentActivity);
		//Remove it
        SharedPreferences prefs = parentActivity.getSharedPreferences("com.google.android.gcm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
		editor.putString("regId", "");
		editor.commit();
        //Check it's been removed
        String newRegistrationId = prefs.getString("regId", "");
        assertEquals(newRegistrationId, "");
        //Reregister
        parentActivity.registerWithGCM();
    	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mainThreadHandler = (RegisterHandler) parentActivity.getRegisterHandler();
    	assertTrue(mainThreadHandler.isHandledMessage());
    }
    */
}
