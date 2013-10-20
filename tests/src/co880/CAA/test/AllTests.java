package co880.CAA.test;

import co880.CAA.Model.EventManager;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(ActivityPendingManagerTest.class);
		suite.addTestSuite(RegisterUserTest.class);
		suite.addTestSuite(RetrieveGoogleCalendarIdNoTest.class);
		suite.addTestSuite(RegisterHandlerAndThreadTest.class);
		suite.addTestSuite(SessionHandlerAndThreadTest.class);
		suite.addTestSuite(CAAActivityTest.class);
		suite.addTestSuite(BoundaryCheckTest.class);
		//suite.addTestSuite(BoundaryOverlayTest.class);
		suite.addTestSuite(CalendarModelTest.class);
		suite.addTestSuite(CalendarObserverTest.class);
		suite.addTestSuite(ContactsModelTest.class);
		suite.addTestSuite(CreateEventTest.class);
		suite.addTestSuite(CheckContactsHandlerAndThreadTest.class);
		suite.addTestSuite(DeleteLocationDataTest.class);
		suite.addTestSuite(EventModelTest.class);
		//suite.addTestSuite(EventModelTest2.class);
		suite.addTestSuite(EventIDCheckerTest.class);
		suite.addTestSuite(FriendManagerActivityTest.class);
		suite.addTestSuite(GetOtherLocationsTest.class);
		suite.addTestSuite(GetUsersLocHandlerTest.class); //Runs OK on its own
		//suite.addTestSuite(GetUsersLocThreadTest.class);
		suite.addTestSuite(JSONArrayParserTest.class);
		suite.addTestSuite(JSONParserTest.class);
		suite.addTestSuite(LocationActivityTest.class);
		suite.addTestSuite(MyItemizedOverlayTest.class);
		suite.addTestSuite(OtherItemizedOverlayTest.class);
		suite.addTestSuite(ProfileActivityTest.class);
		suite.addTestSuite(RawContactManagerTest.class);
		suite.addTestSuite(ReadJSONStreamTest.class);
		suite.addTestSuite(SendLocationDataTest.class);
		suite.addTestSuite(SendEventDetailsTest.class);
		suite.addTestSuite(TimeConverterTest.class);
		//$JUnit-END$
		return suite;
	}

}
