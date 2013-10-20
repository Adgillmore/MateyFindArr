package co880.CAA.test;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashSet;

import android.test.ActivityInstrumentationTestCase2;
import co880.CAA.Activities.FriendManagerActivity;

public class CheckContactsHandlerAndThreadTest extends ActivityInstrumentationTestCase2<FriendManagerActivity> {
	
	private FriendManagerActivity friendMan;

	public CheckContactsHandlerAndThreadTest() {
		super("co880.CAA.Activities", FriendManagerActivity.class);
	}

	protected void setUp() throws Exception {
		friendMan = getActivity();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCheckUsersHandler() {
		ArrayList<String> users = new ArrayList<String>();
		users.add("god@gmail.com");
		friendMan.getCheckUsers(users);
		assertNotNull(friendMan.getCheckUsersHandler());
		friendMan.endProgressDialog();
	}
	

	public void testCheckUsersThread() {
		ArrayList<String> users = new ArrayList<String>();
		users.add("god@gmail.com");
		friendMan.getCheckUsers(users);
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		assertTrue(friendMan.getCheckConThrd().getState() == State.TERMINATED);
	}
	
	
	public void testCreateCheckContactsThread() {
		friendMan.setCreateRawContactRequest(true);
		ArrayList<String> users = new ArrayList<String>();
		users.add("god@gmail.com");
		friendMan.getCheckUsers(users);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		assertEquals(true, friendMan.getQueryListViewReturned());
		friendMan.getRawContact().deleteRawContact(friendMan,
				"god@gmail.com");
	}

	
	public void testFindFriends() {
		HashSet<String> orson = new HashSet<String>();
		orson.add("orson8989080889@gmail.com");
		friendMan.checkLocalAndCrossCheckWithServer(orson);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(true, friendMan.getQueryListViewReturned());
		assertEquals(false, friendMan.getCheckedContacts().contains("orson8989080889@gmail.com"));
	}
	
}
