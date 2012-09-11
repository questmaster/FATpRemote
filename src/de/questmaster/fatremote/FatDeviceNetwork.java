/**
 * 
 */
package de.questmaster.fatremote;

import java.io.IOException;
import java.util.List;

import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * This interface abstracts the Android Socket to be able to mock it for testing.
 * 
 * @author daniel
 */
public interface FatDeviceNetwork {

	/**
	 * Discovers all FreeAgent devices on the Network. It sends a 
	 * discovery message and returns the devices that answered in
	 * a list.
	 * 
	 * @return List of discovered FreeAgent devices.
	 */
	public List<FATDevice> getFatNetworkDevices();
	
	/**
	 * Sends a given RemoteEvent to the current device and returns an optional answer.
	 * @param event Remote event to be sent.
	 * @return Answer to sent event.
	 */
	public FATRemoteEvent transmitRemoteEvent(FATRemoteEvent event) throws IOException;
	
	/**
	 * Sets the device events are sent to.
	 * @param device The device.
	 */
	public void setFatDevice(FATDevice device);

	/**
	 * Gets the device events are sent to.
	 * @returns The device.
	 */
	public FATDevice getFatDevice();
}
