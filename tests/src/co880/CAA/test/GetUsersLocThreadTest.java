package co880.CAA.test;

import java.util.HashSet;
import java.util.Set;

import co880.CAA.Activities.LocationActivity;
import co880.CAA.ServerUtils.GetUsersLocHandler;
import co880.CAA.ServerUtils.GetUsersLocThread;
import android.test.AndroidTestCase;

public class GetUsersLocThreadTest extends AndroidTestCase {
	private LocationActivity myActivity;
	private GetUsersLocHandler myHandler;
	private GetUsersLocThread myThread;
	private String[] keys;
	private Set<String> users;
	private String expectedResults;
	private boolean threadAlive;

	public GetUsersLocThreadTest() {
		super();
		keys = new String[6];
		keys[0] = "gmail";
		keys[1] = "latitude";
		keys[2] = "longitude";
		keys[3] = "speed";
		keys[4] = "latestTimestamp";
		keys[5] = "status";
		
		users = new HashSet();
		users.add("testdata@gmail.com");
		users.add("testdata2@googlemail.com");
		
		expectedResults = "     [{\"gmail\":\"testdata@gmail.com\",\"0\":\"testdata@gmail.com\"," +
				"\"latitude\":\"12345678\",\"1\":\"12345678\",\"longitude\":\"87654321\",\"2\":\"87654321\"," +
				"\"speed\":\"666\",\"3\":\"666\"," +
				"\"latestTimestamp\":\"1111111111111\",\"4\":\"1111111111111\",\"status\":\"0\",\"5\":\"0\"}," +
				"{\"gmail\":\"testdata2@googlemail.com\",\"0\":\"testdata2@googlemail.com\"," +
				"\"latitude\":\"42424242\",\"1\":\"42424242\",\"longitude\":\"13131313\",\"2\":\"13131313\"," +
				"\"speed\":\"2.99\",\"3\":\"2.99\"," +
				"\"latestTimestamp\":\"9999999999999\",\"4\":\"9999999999999\",\"status\":\"0\",\"5\":\"0\"}]\n" +
				"<!-- Hosting24 Analytics Code -->\n" +
				"<script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>\n" +
				"<!-- End Of Analytics Code -->\n";
	}

	protected void setUp() throws Exception {
		super.setUp();
		myActivity = new LocationActivity();
		myHandler = new GetUsersLocHandler(myActivity, keys);
		myThread = new GetUsersLocThread(myHandler, users, myActivity);
		threadAlive = false;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @author Adam
	 * Tests that the two String arrays are successfully sent to 
	 * the server and retrieved via the ReadJSONStream class
	 */
	public void testValidRun() {
		myThread.start();
		while (myThread.getMessageSent() < 1) {
			//do nothing
		}
		myThread.setQueryServer(false);
		assertEquals(expectedResults, myThread.getMyJSONString());
	}
	
	/**
	 * @author Adam
	 * Tests that the thread is created and set running.
	 */
	public void testSwitchOn() {
		myThread.start();
		while (myThread.getMessageSent() <2) {
			//checks that thread is alive for at least two queries
			threadAlive = isThreadAlive();
		}
		assertTrue(threadAlive);
		myThread.setQueryServer(false);
	}
	
	/**
	 * @author Adam
	 * Tests that the boolean toggle
	 * works to stop the thread collecting data.
	 */
	public void testSwitchDataOff() {
		myThread.setQueryServer(false);
		myThread.start();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(0, myThread.getMessageSent());
		assertEquals(null, myThread.getMyJSONString());
	}
	
	/**
	 * @author Adam
	 * Tests that the boolean toggle
	 * works to terminate the thread.
	 */
	public void testSwitchThreadOff() {
		myThread.setQueryServer(false);
		myThread.start();
		try {
			Thread.sleep(1000); //wait for thread to run and terminate
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(isThreadAlive());
	}
	
	/**
	 * @author Adam
	 * Utility method to check state of the GetUsersLocThread
	 * @return true if thread is not terminated, false if it is.
	 */
	private boolean isThreadAlive() {
		if (myThread.getState() != Thread.State.TERMINATED) {
				threadAlive = true;
		}
		else {
				threadAlive = false;
		}
		return threadAlive;
	}
	
	//Is this a reasonable way to do this or does it risk synchronisation issues?
	/**
	 * @author Adam
	 * Tests that the server query happens
	 * more than once.
	 */
	public void testMessageCount() {
		myThread.start();
		while (myThread.getMessageSent() < 2) { //
			//do nothing
			//note each cycle of the thread takes 30 secs
		}
		myThread.setQueryServer(false);
		assertEquals(2, myThread.getMessageSent());
	}
	
	/**
	 * @author Adam
	 * Tests that the thread Handler receives the message
	 * sent by the thread.
	 */
	public void testMessageSending() {
		myThread.start();
		while (myThread.getMessageSent() < 1) {
			//do nothing
		}
		myThread.setQueryServer(false);
		assertTrue(myHandler.hasMessages(42));
	}
	
}
