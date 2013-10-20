package co880.CAA.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests2 {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests2.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(EventModelTest3.class); //Must run separately due to leaked window
		//$JUnit-END$
		return suite;
	}

}
