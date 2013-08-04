/**
 * 
 */
package de.questmaster.fatremote.testinfrastructure;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import junit.framework.Assert;

import de.questmaster.fatremote.network.FatDeviceNetwork;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * @author daniel
 *
 */
public class MockFatDeviceNetworkImpl implements FatDeviceNetwork {

	private int mEventCount = 0;
	

	/**
	 * 
	 */
	public MockFatDeviceNetworkImpl() {
		mEventCount = 0;
	}

	/* (non-Javadoc)
	 * @see de.questmaster.fatremote.FatDeviceNetwork#getFatNetworkDevices()
	 */
	@Override
	public List<FATDevice> getFatNetworkDevices(InetAddress broadcastAddress) {
		Assert.fail("not implemented");
		return null;
	}

	/* (non-Javadoc)
	 * @see de.questmaster.fatremote.FatDeviceNetwork#transmitRemoteEvent(de.questmaster.fatremote.datastructures.FATRemoteEvent)
	 */
	@Override
	public FATRemoteEvent transmitRemoteEvent(FATRemoteEvent event) throws IOException {
		mEventCount++;
		return null;
	}

	/* (non-Javadoc)
	 * @see de.questmaster.fatremote.FatDeviceNetwork#setFatDevice(de.questmaster.fatremote.datastructures.FATDevice)
	 */
	@Override
	public void setFatDevice(FATDevice device) {
		Assert.fail("not implemented");

	}

	/* (non-Javadoc)
	 * @see de.questmaster.fatremote.FatDeviceNetwork#setFatDevice()
	 */
	@Override
	public FATDevice getFatDevice() {
		Assert.fail("not implemented");
		return null;
	}

	public int getEventCount() {
		return mEventCount;
	}
}
