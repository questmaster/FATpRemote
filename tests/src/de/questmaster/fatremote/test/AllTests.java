package de.questmaster.fatremote.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(FatDevicesDbAdapterTest.class);
		suite.addTestSuite(FATDeviceTest.class);
		//$JUnit-END$
		return suite;
	}

}
