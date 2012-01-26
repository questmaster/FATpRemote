package de.questmaster.fatremote;

import de.questmaster.fatremote.datastructures.test.FATDeviceTest;
import de.questmaster.fatremote.test.FatDevicesDbAdapterTest;
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
