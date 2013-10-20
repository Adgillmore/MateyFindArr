package co880.CAA.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import co880.CAA.ServerUtils.JSONParser;
import co880.CAA.ServerUtils.SendEventDetails;
import co880.CAA.ServerUtils.SendEventThread;


public class SendEventDetailsTest extends AndroidTestCase {

	SendEventDetails seDetails;
	SendEventThread seThread;
	JSONObject JSONCombined;
	ArrayList<String> attendees;
	
	public SendEventDetailsTest() {
		super();
		
		HashMap<String, String> eventMap = new HashMap<String, String>();
		eventMap.put("calendar_id", "666");// Required
		eventMap.put("title", "test");// Optional
		eventMap.put("description", "test description");// Optional
		eventMap.put("dtstart", "00:00"); // Required
		eventMap.put("dtend", "01:00"); // Required
		eventMap.put("eventTimezone", "1");// Required for ICS
		eventMap.put("isUpdate", "false");
		JSONObject JSONEvent = new JSONObject(eventMap);
		
		HashMap<String, Integer> boundaryMap = new HashMap<String, Integer>();
		boundaryMap.put("radius", 0);
		boundaryMap.put("latitude", 0);
		boundaryMap.put("longitude", 0);
		JSONObject JSONBoundary = new JSONObject(boundaryMap);
		
		HashMap<String, String> attendeeMap = new HashMap<String, String>();
		attendeeMap.put("attendee1", "mimicdanjay@googlemail.com");
		attendeeMap.put("attendee2", "adgillmore@gmail.com");
		attendeeMap.put("attendee3", "kalanleung@googlemail.com");
		JSONObject JSONAttendee = new JSONObject(attendeeMap);
		
		JSONCombined = new JSONObject();
		try {
			JSONCombined.put("eventObject", JSONEvent);
			JSONCombined.put("attendeeObject", JSONAttendee);
			JSONCombined.put("boundaryObject", JSONBoundary);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		attendees = new ArrayList<String>();
		attendees.add("mimicdanjay@googlemail.com");
		attendees.add("adgillmore@gmail.com");
		attendees.add("kalanleung@googlemail.com");
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		seDetails = new SendEventDetails();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testShareEvent() {
		@SuppressWarnings("unused")
		InputStream is = seDetails.shareEvent(JSONCombined, attendees);
		int end = seDetails.getResult().indexOf("<");
		String result = seDetails.getResult().substring(0, end);
		JSONParser jp = new JSONParser();
		String[] failure = new String[1];
		failure[0] = "failure";
		String[] result2 = jp.parse(result, failure, false);
		String result3 = result2[0];
		assertEquals("0", result3);
	}

}
