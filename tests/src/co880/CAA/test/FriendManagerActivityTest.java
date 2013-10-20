package co880.CAA.test;

import java.util.ArrayList;
import java.util.HashSet;

import co880.CAA.Activities.CreateEvent;
import co880.CAA.Activities.FriendManagerActivity;
import co880.CAA.Model.RawContactManager;
import android.provider.ContactsContract.RawContacts;
import android.test.ActivityInstrumentationTestCase2;

public class FriendManagerActivityTest extends
		ActivityInstrumentationTestCase2<CreateEvent> {

	private FriendManagerActivity friendManager;
	private CreateEvent createEvent;

	public FriendManagerActivityTest() {
		super("co880.CAA.CreateEvent", CreateEvent.class);
	}

	protected void setUp() throws Exception {
		createEvent = getActivity();
		friendManager = new FriendManagerActivity();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testconvertHashSetToArrayList() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < 9; i++) {
			list.add("" + i);
		}
		HashSet<String> nHashset = friendManager
				.convertArrayListToHashSet(list);
		assertEquals(9, nHashset.size());
	}

	public void testConvertArrayListToHashSet() {
		HashSet<String> list = new HashSet<String>();
		for (int i = 0; i < 9; i++) {
			list.add("" + i);
		}
		ArrayList<String> nArrayList = friendManager
				.convertHashSetToArrayList(list);
		assertEquals(9, nArrayList.size());
	}

	/*
	public void testCheckLocalAndCrossCheckWithServer() {
		friendManager.setCreateRawContactRequest(true);
		HashSet<String> email = new HashSet<String>();
		email.add("yoda@gmail.com");
		try {
			friendManager.checkLocalAndCrossCheckWithServer(email);
			Thread.sleep(300000);
		} catch (Exception e) {

		}
		
		RawContactManager rawContact = new RawContactManager();
		ArrayList<String> nEmail = friendManager.convertHashSetToArrayList(email);
		ArrayList<String> returnEmail = rawContact.checkAndReturnContacts(createEvent, nEmail);
		assertEquals(0, returnEmail.size());

	}
	*/
	
	//Test one selection
	public void testGetSelection() {
		CharSequence[] friends = {"Adam", "Bob", "Carrie", "Dean"};
		boolean[] booleanList = {true, false, false, false};
		ArrayList<String> selectedFriend = friendManager.getSelections(booleanList, friends);
		assertEquals("Adam", selectedFriend.get(0));
	}
	
	//Test Multiple selections
	public void testGetSelections() {
		CharSequence[] friends = {"Adam", "Bob", "Carrie", "Dean"};
		boolean[] booleanList = {true, false, true, false};
		ArrayList<String> selectedFriend = friendManager.getSelections(booleanList, friends);
		assertEquals(true , selectedFriend.contains("Adam") && selectedFriend.contains("Carrie"));
	}

}
