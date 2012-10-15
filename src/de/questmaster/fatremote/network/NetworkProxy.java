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
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import de.questmaster.fatremote.DebugHelper;
import de.questmaster.fatremote.FatRemoteSettings;
import de.questmaster.fatremote.R;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.R.drawable;
import de.questmaster.fatremote.R.id;
import de.questmaster.fatremote.R.string;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * This class handles all network interaction. Therefore it is implemented as a singleton, as only one 
 * message at a time may be sent or received.
 * 
 * @author daniel
 *
 */
public class NetworkProxy {

	private static final String LOG_TAG = "NetworkProxy";

	private static volatile NetworkProxy singleton = null;
	private FATDevice mFat = null;
	private Activity mBaseActivity = null;
	private BlockingQueue<FATRemoteEvent> mEventList = new ArrayBlockingQueue<FATRemoteEvent>(20, true);
	private Thread mSendingThread = null;
	private FatDeviceNetwork mNetworkAccess = null;
	
	private class SendingThread extends Thread {
		/** Executed by thread */
		public void run() {
			boolean goon = true;
			while (goon) {
				try {
					// get event to send
					FATRemoteEvent event = mEventList.take();

					mNetworkAccess.transmitRemoteEvent(event);
				} catch (final IOException e) {
					mBaseActivity.runOnUiThread(new Runnable() {
							/** Executed by thread */
							public void run() {
								Toast.makeText(mBaseActivity, e.getMessage(), Toast.LENGTH_LONG).show();
							}
						});
				} catch (InterruptedException e) {
					goon = false; // close thread on closing of remote
					Log.e(LOG_TAG, e.getLocalizedMessage(), e);
				}

				mBaseActivity.runOnUiThread(new Runnable() {
					/** Executed by thread */
					public void run() { // reset send image
						ImageView sending = (ImageView) mBaseActivity.findViewById(R.id.sendLED);
						if (sending != null) {
							sending.setImageResource(R.drawable.light_normal);
						}
					}
				});
			}
			Log.i(LOG_TAG, "Thread exited.");
		}
	}

	/**
	 * Retrieve an instance of this object. If none exists, it is created.
	 * 
	 * @param c Context of the activity
	 * @return Instance of this class
	 */
	public static NetworkProxy getInstance(Activity c) {

		if (c == null) {
			throw new IllegalArgumentException("Activity was null");
		}
			
		if (singleton == null) {
			
			// Initialize proxy
			NetworkProxy np = new NetworkProxy(); 
			
			singleton = np;
			
		}

		if (singleton.mSendingThread == null) {
			// Setup sending thread
			singleton.mSendingThread = singleton.new SendingThread();
			singleton.mSendingThread.start();
			
		}
		
		// read current Fat data
		AppSettings settings = new FatRemoteSettings.AppSettings();
		settings.readSettings(c);
		singleton.mFat = settings.getFat();
		
		assert singleton.mFat != null;
		
		singleton.mNetworkAccess = new FatDeviceNetworkImpl(singleton.mFat);
				
		singleton.mBaseActivity = c;

		return singleton;
	}

	/**
	 * Checks if Wifi is enabled and connected to a network.
	 * 
	 * @return true - if available, false - otherwise
	 */
	public boolean isWifiEnabled() {
		WifiManager wifiManager = (WifiManager) mBaseActivity.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager == null || (!DebugHelper.ON_EMULATOR && !wifiManager.isWifiEnabled())) {
			return false;
		}

		return true;
	}

	InetAddress getBroadcastAddress() throws IOException {
		InetAddress result = null;
		WifiManager wifi = (WifiManager) mBaseActivity.getSystemService(Context.WIFI_SERVICE);

		if (wifi != null) {
			DhcpInfo dhcp = wifi.getDhcpInfo();

			if (dhcp != null) {
				int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
				byte[] quads = new byte[4];
				for (int k = 0; k < 4; k++) {
					quads[k] = (byte) ((broadcast >> (k * 8)) & 0xFF);
				}
				result = InetAddress.getByAddress(quads);
			}
		}
		return result;
	}
	
	/**
	 * Sends discovery message to the local Wifi. If a FAT+ device answers this 
	 * data is returned to the caller. Throws ConnectionException if not connected 
	 * to Wifi.
	 * 
	 * @return List of devices that answered to the discovery message
	 * @throws ConnectException
	 */
	public List<FATDevice> discoverFAT() throws IOException {
		List<FATDevice> adr = null;

		// Check wifi availability
		if (isWifiEnabled()) {

			// get detected fat's
			adr = mNetworkAccess.getFatNetworkDevices(getBroadcastAddress());
		} else {
			throw new ConnectException(mBaseActivity.getResources().getString(R.string.app_err_enwifi));
		}
				
		return adr;
	}

	/**
	 * Enqueues a remote event to the processing queue. If no sending thread is available one is created.
	 * 
	 * @param event Event to be enqueued
	 */
	public void addRemoteEvent (FATRemoteEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("null event.");
		}
		
		if (mFat == null) {
			throw new IllegalStateException("FAT device not configured before using sendCode().");
		}
				
		try {
			mEventList.put(event);
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, "Event not in queue (keyId): " + event.getCommandCode()[3], e);
		}
	}
		
	/**
	 * Clears the sending queue and stops the sending thread, if one is running.
	 */
	public void dismissRemoteEvents() {
		mEventList.clear();
		if (mSendingThread != null) {
			mSendingThread.interrupt();
			mSendingThread = null; 
		}
	}

	/**
	 * Sets the FatDeviceNetwork implementation used. This method is mostly used for testing.
	 * @param impl FatDeviceNetwork implementation
	 */
	public void setFatDeviceNetwork(FatDeviceNetwork impl) {
		mNetworkAccess = impl;
	}
	
}
