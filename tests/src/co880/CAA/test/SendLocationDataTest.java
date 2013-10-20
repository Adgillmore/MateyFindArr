package co880.CAA.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;

import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.LocationActivity;
import co880.CAA.ServerUtils.DeleteLocationData;
import co880.CAA.ServerUtils.GetOtherLocations;
import co880.CAA.ServerUtils.ReadJSONStream;
import co880.CAA.ServerUtils.SendLocationData;

public class SendLocationDataTest extends ActivityInstrumentationTestCase2<LocationActivity> {
	
	private LocationActivity parentActivity;
	private SendLocationData sendLoc;
	private String[] locationDetails;
	private String[] keys;
	private DeleteLocationData deleteData;
	private GetOtherLocations getOthLoc;
	private String expectedResults;

	public SendLocationDataTest() {
		super("co880.CAA.Activities", LocationActivity.class);
		
		locationDetails = new String[6];
		locationDetails[0] = "testSend@gmail.com";
		locationDetails[1] = "22222222";
		locationDetails[2] = "33333333";
		locationDetails[3] = "666";
		locationDetails[4] = "1234567890123";
		locationDetails[5] = "0";
		
		/*
		keys = new String[6];
		keys [0] = "gmail";
		keys [1] = "latitude";
		keys [2] = "longitude";
		keys [3] = "speed";
		keys [4] = "latestTimestamp"; 
		keys [5] = "status";
		
		expectedResults = "     [{\"gmail\":\"testSend@gmail.com\",\"0\":\"testSend@gmail.com\"," +
				"\"latitude\":\"22222222\",\"1\":\"22222222\",\"longitude\":\"33333333\",\"2\":\"33333333\"," +
				"\"speed\":\"666\",\"3\":\"666\",\"latestTimestamp\":\"1234567890123\",\"4\":\"1234567890123\", \"status\":\"0\",\"5\":\"0\"}]\n" +
				"<!-- Hosting24 Analytics Code -->\n" +
				"<script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>\n" +
				"<!-- End Of Analytics Code -->\n";
				
		getOthLoc = new GetOtherLocations();
		*/
	}

	protected void setUp() throws Exception {
		super.setUp();
		parentActivity = getActivity(); 
		deleteData = new DeleteLocationData(parentActivity); 
		deleteData.execute("testSend@gmail.com");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSendGoodData() {
		try {
			Thread.sleep(5000); //Wait for delete to occur
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendLoc = new SendLocationData(parentActivity); 
		sendLoc.execute(locationDetails);
		
		try { //Need to give time for the AsyncTask to process the postparameters
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<NameValuePair> params = sendLoc.getPostParameters();
		
		NameValuePair nameValue1 = params.get(0);
		assertEquals("testSend@gmail.com", nameValue1.getValue());
		assertEquals("gmail", nameValue1.getName());
		
		NameValuePair nameValue2 = params.get(1);
		assertEquals("22222222", nameValue2.getValue());
		assertEquals("latitude", nameValue2.getName());
		
		NameValuePair nameValue3 = params.get(2);
		assertEquals("33333333", nameValue3.getValue());
		assertEquals("longitude", nameValue3.getName());
		
		NameValuePair nameValue4 = params.get(3);
		assertEquals("666", nameValue4.getValue());
		assertEquals("speed", nameValue4.getName());
		
		NameValuePair nameValue5 = params.get(4);
		assertEquals("1234567890123", nameValue5.getValue());
		assertEquals("timestamp", nameValue5.getName());
		
		NameValuePair nameValue6 = params.get(5);
		assertEquals("0", nameValue6.getValue());
		assertEquals("status", nameValue6.getName());
		
	}
		
	public void testSendBadData() {
		try {
			Thread.sleep(5000); //Wait for delete to occur
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		locationDetails[4] = "";
		sendLoc = new SendLocationData(parentActivity); 
		sendLoc.execute(locationDetails);
		
		try { //Need to give time for the AsyncTask to process the postparameters
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<NameValuePair> params = sendLoc.getPostParameters();
		
		NameValuePair nameValue1 = params.get(0);
		assertEquals("testSend@gmail.com", nameValue1.getValue());
		assertEquals("gmail", nameValue1.getName());
		
		NameValuePair nameValue2 = params.get(1);
		assertEquals("22222222", nameValue2.getValue());
		assertEquals("latitude", nameValue2.getName());
		
		NameValuePair nameValue3 = params.get(2);
		assertEquals("33333333", nameValue3.getValue());
		assertEquals("longitude", nameValue3.getName());
		
		NameValuePair nameValue4 = params.get(3);
		assertEquals("666", nameValue4.getValue());
		assertEquals("speed", nameValue4.getName());
		
		NameValuePair nameValue5 = params.get(4);
		assertEquals("", nameValue5.getValue());
		assertEquals("timestamp", nameValue5.getName());
		
		NameValuePair nameValue6 = params.get(5);
		assertEquals("0", nameValue6.getValue());
		assertEquals("status", nameValue6.getName());
		//check that the database write has occurred
	}
}
