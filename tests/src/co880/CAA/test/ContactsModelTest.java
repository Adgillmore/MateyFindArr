/**
 * 
 */
package co880.CAA.test;

import java.util.ArrayList;
import java.util.HashSet;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Model.ContactsModel;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

/**
 * @author Ka Lan
 *
 */
public class ContactsModelTest extends ActivityInstrumentationTestCase2<CAAActivity> {
	
	private ContactsModel conMod;
	private CAAActivity mContext;
	private ArrayList<String> regUsers;

	/**
	 * @param name
	 */
	public ContactsModelTest() {
		super("co880.CAA", CAAActivity.class);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		mContext = getActivity();
		conMod = new ContactsModel(mContext);
		regUsers = new ArrayList<String>();
		regUsers.add("9999999999999@gmail.com");
		regUsers.add("11111111111111@googlemail.com");
		regUsers.add("66666777777@googlemail.com");
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testReturnLocalGmails() {
		HashSet<String> localGmails = conMod.returnLocalGmails();
		assertEquals(false, localGmails.isEmpty());
		}
	
	public void testReturnMyGmail() {
		HashSet<String> localGmails = conMod.returnLocalGmails();
		assertEquals(false, localGmails.contains(mContext.getpref().getString("email", null)));
	}
	
	public void testReturnMatched() {
		HashSet<String> newGmails = conMod.returnMatched(regUsers);
		assertEquals(false, newGmails.contains("9999999999999@gmail.com"));
	}

}
