/*
 * Copyright (C) 2012 Daniel Jacobi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.questmaster.fatremote.network;

import java.io.IOException;
import java.net.InetAddress;
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
	 * @param broadcastAddress Broadcastaddress used to send discovery packet to.
	 * @return List of discovered FreeAgent devices.
	 */
	List<FATDevice> getFatNetworkDevices(InetAddress broadcastAddress);
	
	/**
	 * Sends a given RemoteEvent to the current device and returns an optional answer.
	 * @param event Remote event to be sent.
	 * @return Answer to sent event.
	 */
	FATRemoteEvent transmitRemoteEvent(FATRemoteEvent event) throws IOException;
	
	/**
	 * Sets the device events are sent to.
	 * @param device The device.
	 */
	void setFatDevice(FATDevice device);

	/**
	 * Gets the device events are sent to.
	 * @returns The device.
	 */
	FATDevice getFatDevice();
}
