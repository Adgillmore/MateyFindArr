package co880.CAA.test;

import java.util.ArrayList;

import co880.CAA.Activities.FriendManagerActivity;
import co880.CAA.Model.RawContactManager;
import android.test.ActivityInstrumentationTestCase2;
import junit.framework.TestCase;

public class RawContactManagerTest extends ActivityInstrumentationTestCase2<FriendManagerActivity> {
	
	private FriendManagerActivity friendMan;
	private RawContactManager rawCon;
	private String orson;
	private String welles;

	public RawContactManagerTest() {
		super("co880.CAA.Activities", FriendManagerActivity.class);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		friendMan = getActivity();
		rawCon = new RawContactManager();
		orson = "orson@gmail.com";
		welles = "welles@gmail.com";
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDeleteRawContact() {
		rawCon.insertNewContact(friendMan, orson);
		rawCon.deleteRawContact(friendMan, orson);
		ArrayList<String> rawContacts = rawCon.getRawContacts(friendMan);
		assertEquals(false, rawContacts.contains(orson));
	}
	
	public void testInsertNewContact() {
		rawCon.insertNewContact(friendMan, orson);
		ArrayList<String> rawContacts = rawCon.getRawContacts(friendMan);
		assertEquals(true, rawContacts.contains(orson));
		rawCon.deleteRawContact(friendMan, orson);
	}
	
	public void testInsertNewContacts() {
		rawCon.insertNewContact(friendMan, orson);
		rawCon.insertNewContact(friendMan, "welles");
		ArrayList<String> rawContacts = rawCon.getRawContacts(friendMan);
		assertEquals(true, rawContacts.contains(orson) && rawContacts.contains("welles"));
		rawCon.deleteRawContact(friendMan, orson);
		rawCon.deleteRawContact(friendMan, "welles");
	}
	
	public void testInsertCreateBulkContacts() {
		ArrayList<String> bulkContacts = new ArrayList<String>();
		bulkContacts.add(orson);
		bulkContacts.add(welles);
		bulkContacts.add("kane@gmail.com");
		rawCon.createBulkContacts(friendMan, bulkContacts);
		ArrayList<String> rawContacts = rawCon.getRawContacts(friendMan);
		assertEquals(true, (rawContacts.contains(orson) && rawContacts.contains(welles) && rawContacts.contains("kane@gmail.com")));
		rawCon.deleteRawContact(friendMan, orson);
		rawCon.deleteRawContact(friendMan, welles);
		rawCon.deleteRawContact(friendMan, "kane@gmail.com");
	}
	
	public void testGetDeletedContacts() {
		rawCon.insertNewContact(friendMan, orson);
		rawCon.deleteRawContact(friendMan, orson);
		ArrayList<String> deletedContacts = rawCon.getDeletedContacts(friendMan);
		assertEquals(true, deletedContacts.contains(orson));
	}
	
	public void testCheckAndReturnContacts() {
		rawCon.insertNewContact(friendMan, orson);
		ArrayList<String> orsonList = new ArrayList<String>();
		orsonList.add(orson);
		ArrayList<String> returnedContacts = rawCon.checkAndReturnContacts(friendMan, orsonList);
		assertEquals(true, returnedContacts.size() == 0);
		rawCon.deleteRawContact(friendMan, orson);
	}
}
