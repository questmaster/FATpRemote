package de.questmaster.fatremote;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.questmaster.fatremote.datastructures.test.FATDeviceTest;
import de.questmaster.fatremote.datastructures.test.FATRemoteEventTest;
import de.questmaster.fatremote.test.FatDevicesDbAdapterTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(FatDevicesDbAdapterTest.class);
//		suite.addTestSuite(NetworkProxyTest.class);
		suite.addTestSuite(FATDeviceTest.class);
		suite.addTestSuite(FATRemoteEventTest.class);
		//$JUnit-END$
		return suite;
	}

}
