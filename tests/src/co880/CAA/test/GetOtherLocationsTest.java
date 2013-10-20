package co880.CAA.test;

import java.io.InputStream;
import java.util.HashSet;

import android.test.AndroidTestCase;
import co880.CAA.ServerUtils.GetOtherLocations;

public class GetOtherLocationsTest extends AndroidTestCase {
	private GetOtherLocations getOthLocs;
	private HashSet<String> users;
	private InputStream myStream;
	
	public GetOtherLocationsTest() {
		super();
		getOthLocs = new GetOtherLocations();
		users = new HashSet<String>();
		users.add("testdata@gmail.com");
		users.add("testdata2@googlemail.com");
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @author Adam
	 * Tests that the list of users is sent to 
	 * the server and that the response is not null.
	 * Note: main functionality of this class is tested 
	 * by ReadJSONStreamTest and GetUsersOtherLocThreadTest
	 */
	public void testHTTPRequest() {
		myStream = getOthLocs.sendRequest(users);
		assertNotNull(myStream);
	}
}
