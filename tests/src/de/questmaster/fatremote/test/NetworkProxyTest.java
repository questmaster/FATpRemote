/**
 * 
 */
package de.questmaster.fatremote.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;
import junit.framework.Assert;
import junit.framework.TestCase;
import de.questmaster.fatremote.NetworkProxy;
import de.questmaster.fatremote.RemoteActivity;
import de.questmaster.fatremote.StartActivity;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;
import de.questmaster.fatremote.testinfrastructure.IntentCatchingActivityUnitTestCase;
import de.questmaster.fatremote.testinfrastructure.MockFatDeviceNetworkImpl;
import de.questmaster.fatremote.testinfrastructure.SettingsContext;

/**
 * @author daniel
 *
 */
public class NetworkProxyTest extends ActivityInstrumentationTestCase2<RemoteActivity> {

	private NetworkProxy mTestee = null;
	private MockFatDeviceNetworkImpl mockNetworkAccess = null; 
	
	/**
	 * @param name
	 */
	public NetworkProxyTest(String name) {
		super(RemoteActivity.class);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Activity act = launchActivity("de.questmaster.fatremote", RemoteActivity.class, null);
		
		mTestee = NetworkProxy.getInstance(act);
		Assert.assertNotNull(mTestee);
		
		mockNetworkAccess = new MockFatDeviceNetworkImpl();
		mTestee.setFatDeviceNetwork(mockNetworkAccess);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		mTestee = null;
		mockNetworkAccess = null;
	}

	public void testSingleton() {
		Activity act = launchActivity("de.questmaster.fatremote", RemoteActivity.class, null);
		NetworkProxy actual = NetworkProxy.getInstance(act);
		
		Assert.assertSame(mTestee, actual);
	}
	
	public void testIsWifiEnabled() {
		boolean expected = false;
		
		Assert.assertEquals(expected, mTestee.isWifiEnabled());
	}
	
	public void testDiscoverDevices() {
		Assert.fail("not implemented");
	}
	
	public void testDissmissRemoteEvents() {
		int notExpected = 4;

		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		
		mTestee.dismissRemoteEvents();
		
		Assert.assertFalse(notExpected == mockNetworkAccess.getEventCount());
	}
	
	public void testAddRemoteEvent() {
		int expected = 4;
		
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		mTestee.addRemoteEvent(new FATRemoteEvent((short) 0, (short) 0));
		
		Assert.assertEquals(expected, mockNetworkAccess.getEventCount());
	}
}
