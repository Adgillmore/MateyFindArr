package co880.CAA.test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.ServerUtils.DeleteLocationData;
import co880.CAA.ServerUtils.GetOtherLocations;
import co880.CAA.ServerUtils.JSONParser;
import co880.CAA.ServerUtils.ReadJSONStream;
import co880.CAA.ServerUtils.SendLocationData;
import android.test.ActivityInstrumentationTestCase2;

public class DeleteLocationDataTest extends ActivityInstrumentationTestCase2<CAAActivity> {
	private CAAActivity parentActivity;
	private String user;
	private String latitude;
	private String longitude;
	private String timestamp;
	private String speed;
	private String[] keys;
	private SendLocationData upload;
	private DeleteLocationData deleteData;

	public DeleteLocationDataTest() {
		super("co880.CAA.Activities", CAAActivity.class);
		}

	protected void setUp() throws Exception {
		super.setUp();
		parentActivity = getActivity();
		user = ("testDelete@gmail.com");
		upload = new SendLocationData(parentActivity);
		upload.execute("testDelete@gmail.com", "44444444", "55555555",  "25", "1234567890123", "0");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDelete() {
		parentActivity = getActivity();
		deleteData = new DeleteLocationData(
				parentActivity);
		deleteData.execute(user);
		
		try { //Need to give time for the AsyncTask to process the postparameters
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NameValuePair nameValue = deleteData.getPostParameters().get(0);
		assertEquals("testDelete@gmail.com", nameValue.getValue());
		assertEquals("gmail", nameValue.getName());
	}
	

}
