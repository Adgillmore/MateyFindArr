package co880.CAA.test;

import co880.CAA.Activities.CAAActivity;
import co880.CAA.AlarmIntent.ActivityPendingManager;
import android.app.PendingIntent;
import android.test.ActivityInstrumentationTestCase2;

public class LocationActivityPendingManagerTest extends
		ActivityInstrumentationTestCase2<CAAActivity> {
	
	private CAAActivity caaAct;
	private ActivityPendingManager LocActPenMan;

	public LocationActivityPendingManagerTest() {
		super("co880.CAA.Activities", CAAActivity.class);
	}

	protected void setUp() throws Exception {
		caaAct = getActivity();
		LocActPenMan = new ActivityPendingManager(caaAct);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSendPendingIntent() {
		LocActPenMan.sendPendingIntent(666, System.currentTimeMillis() + (20*1000), true);
		assertTrue(LocActPenMan.getPendingIntentsHashMap().containsKey(666));
		assertNotNull(LocActPenMan.getPendingIntentsHashMap().get(666));
		
		LocActPenMan.deletePendingIntent(666);
	}
	
	public void testEditPendingIntent() {
		long startTime1 = System.currentTimeMillis() + (20*1000);
		long startTime2 = System.currentTimeMillis() + (40*1000);
		LocActPenMan.sendPendingIntent(666, startTime1, true);
		PendingIntent raspberry = LocActPenMan.getPendingIntentsHashMap().get(666);
		LocActPenMan.editPendingIntent(666, startTime2, true);
		PendingIntent pi = LocActPenMan.getPendingIntentsHashMap().get(666);
		assertNotSame(raspberry, pi);
		
		LocActPenMan.deletePendingIntent(666);
	}
	
	public void testDeletePendingIntent() {
		LocActPenMan.sendPendingIntent(666, System.currentTimeMillis() + (20*1000), true);
		LocActPenMan.deletePendingIntent(666);
		assertEquals(true, LocActPenMan.getPendingIntentsHashMap().isEmpty());
	}
	



}
