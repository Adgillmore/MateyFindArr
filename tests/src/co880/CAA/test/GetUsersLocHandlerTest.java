package co880.CAA.test;

import android.os.Bundle;
import android.os.Message;
import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.ServerUtils.GetUsersLocHandler;

public class GetUsersLocHandlerTest extends ActivityInstrumentationTestCase2<LocationActivity> {	
	
	private GetUsersLocHandler myHandler;
	private LocationActivity parentActivity;
	private String[] keys;
	private Message message;
	private String expectedJSONString;

	public GetUsersLocHandlerTest() {
		super("co880.CAA.Activities", LocationActivity.class);
			
		keys = new String[6];
		keys [0] = "gmail";
		keys [1] = "latitude";
		keys [2] = "longitude";
		keys [3] = "speed";
		keys [4] = "latestTimestamp"; 
		keys [5] = "status";

		expectedJSONString = "[{\"gmail\":\"adgillmore@gmail.com\",\"0\":\"adgillmore@gmail.com\"," +
				"\"latitude\":\"51270910\",\"1\":\"51270910\",\"longitude\":\"1100853\",\"2\":\"1100853\"," +
				"\"speed\":\"0\",\"3\":\"0\",\"latestTimestamp\":\"1342044790941\",\"4\":\"1342044790941\",\"status\":\"0\",\"5\":\"0\"}," +
				"{\"gmail\":\"mimicdanjay@googlemail.com\",\"0\":\"mimicdanjay@googlemail.com\"," +
				"\"latitude\":\"51291436\",\"1\":\"51291436\",\"longitude\":\"1065340\",\"2\":\"1065340\"," +
				"\"speed\":\"0\",\"3\":\"0\",\"latestTimestamp\":\"1342114088490\",\"4\":\"1342114088490\",\"status\":\"0\",\"5\":\"0\"}]";
	}

	protected void setUp() throws Exception {
		super.setUp();
		parentActivity = getActivity();
		myHandler = (GetUsersLocHandler) parentActivity.getGetLocationsHandler(); 
		
		message = myHandler.obtainMessage();
		message.setData(convertToBundle(expectedJSONString));
		myHandler.sendMessage(message);
		//Something to call handleMessage or to notify thread that 
		//message has been handled.
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @author Adam
	 * Tests that String is accurately retrieved from the message sent to the handler.
	 */
	public void testGetJSONString() {
		String actualJSONString = myHandler.retrieveStringFromMessage(message);
		assertEquals(expectedJSONString, actualJSONString);
	}

	public void testDrawOthers() {
		try {
			Thread.sleep(10000); //wait for message queue to process handleMessage()
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		int userCount = parentActivity.getOtherItemisedOverlay().size();
		assertEquals(2, userCount);
	}
		
	private Bundle convertToBundle(String value) {
		Bundle b = new Bundle();
		b.putString("locations", value);
		return b;
	}

}
