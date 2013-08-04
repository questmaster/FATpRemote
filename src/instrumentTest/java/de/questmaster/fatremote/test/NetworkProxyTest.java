/**
 * 
 */
package de.questmaster.fatremote.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.questmaster.fatremote.network.NetworkProxy;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;
import de.questmaster.fatremote.testinfrastructure.MockFatDeviceNetworkImpl;

/**
 * @author daniel
 *
 */
public class NetworkProxyTest extends TestCase {

	private NetworkProxy mTestee = null;
	private MockFatDeviceNetworkImpl mockNetworkAccess = null; 
	
	/**
	 * @param name
	 */
	public NetworkProxyTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
				
		mTestee = NetworkProxy.getInstance(null);
		Assert.assertNotNull(mTestee);
		
		mockNetworkAccess = new MockFatDeviceNetworkImpl();
		mTestee.setFatDeviceNetwork(mockNetworkAccess);
		
		Assert.assertNotNull(mockNetworkAccess);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		mTestee.dismissRemoteEvents();
		
		mTestee = null;
		mockNetworkAccess = null;
	}

	public void testSingleton() {
		NetworkProxy actual = NetworkProxy.getInstance(null);
		
		Assert.assertSame(mTestee, actual);
	}
	
	public void testIsWifiEnabled() {
		boolean expected = false;
		
		Assert.assertEquals(expected, mTestee.isWifiEnabled());
	}
	
	public void testDiscoverDevices() {


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
