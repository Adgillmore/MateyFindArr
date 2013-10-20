package co880.CAA.test;

import android.test.AndroidTestCase;
import co880.CAA.ServerUtils.JSONParser;
import junit.framework.TestCase;

public class JSONParserTest extends AndroidTestCase {
	private JSONParser myParser;
	private String inputString1;
	private String inputString2;
	private String inputString3;
	private String [] keys;
	private String [] actualResults;

	public JSONParserTest() {
		super();
		myParser = new JSONParser();
		/*
		keys = new String[5];
		keys [0] = "gmail";
		keys [1] = "latitude";
		keys [2] = "longitude";
		keys [3] = "speed";
		keys [4] = "latestTimestamp"; 
		*/

		inputString1 = "[{\"gmail\":\"adgillmore@gmail.com\"}]";
		inputString2 = "[{\"gmail\":\"adgillmore@gmail.com\",\"0\":\"adgillmore@gmail.com\"," +
				"\"latitude\":\"51270910\",\"1\":\"51270910\"," +
				"\"longitude\":\"1100853\",\"2\":\"1100853\"}]";
		inputString3 = "An invalid String";
		
		//inputString1 = "{\"calendar_id\":\"2\",\"title\":\"two \",\"dtstart\":\"1343832120000\",\"dtend\":\"1343832480000\",\"description\":\"www\",\"eventTimezone\":\"Europe\\/London\"},\"attendeeObject\":{\"attendee0\":\"adgillmore@gmail.com\",\"attendee1\":\"kalanleung@googlemail.com\"}}";
	}

	protected void setUp() throws Exception {
		super.setUp();
		actualResults = new String[1];
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCorrectSingleParse() {
		keys = new String[1];
		keys [0] = "gmail";
		actualResults = myParser.parse(inputString1, keys, false);
		String[] expectedResults = {"adgillmore@gmail.com"}; 
		assertEquals(expectedResults[0], actualResults[0]);
	
	}
	

}
