package co880.CAA.test;

import android.test.AndroidTestCase;
import co880.CAA.ServerUtils.JSONParser;

public class JSONArrayParserTest extends AndroidTestCase {
	
	private JSONParser myParser;
	private String inputString1;
	private String inputString2;
	private String [] keys;
	private String [][] actualResults;


	public JSONArrayParserTest() {
		super();
		myParser = new JSONParser();
		actualResults = new String[2][5];
		keys = new String[5];
		keys [0] = "gmail";
		keys [1] = "latitude";
		keys [2] = "longitude";
		keys [3] = "speed";
		keys [4] = "latestTimestamp"; 

		inputString1 = "[{\"gmail\":\"adgillmore@gmail.com\",\"0\":\"adgillmore@gmail.com\"," +
				"\"latitude\":\"51270910\",\"1\":\"51270910\",\"longitude\":\"1100853\",\"2\":\"1100853\"," +
				"\"speed\":\"0\",\"3\":\"0\",\"latestTimestamp\":\"1342044790941\",\"4\":\"1342044790941\"}," +
				"{\"gmail\":\"mimicdanjay@googlemail.com\",\"0\":\"mimicdanjay@googlemail.com\"," +
				"\"latitude\":\"51291436\",\"1\":\"51291436\",\"longitude\":\"1065340\",\"2\":\"1065340\"," +
				"\"speed\":\"0\",\"3\":\"0\",\"latestTimestamp\":\"1342114088490\",\"4\":\"1342114088490\"}]";
		inputString2 = "An invalid string";
	}

	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCorrectParse() {
		myParser.twoDParse(inputString1, keys);
		actualResults = myParser.getTwoDValue();
		String[][] expectedResults = {{"adgillmore@gmail.com", "51270910", "1100853", "0", "1342044790941"},
			{"mimicdanjay@googlemail.com", "51291436", "1065340", "0", "1342114088490"}};
		for(int i=0; i<actualResults.length; i++) {
			for(int j=0; j<keys.length; j++) {
				assertEquals(expectedResults[i][j], actualResults[i][j]);
			}
		}
	}
	
	public void testInvalidString() {
		actualResults = new String[2][5];
		myParser.twoDParse(inputString2, keys);
		actualResults = myParser.getTwoDValue();
		assertNull(actualResults);
	}

}
