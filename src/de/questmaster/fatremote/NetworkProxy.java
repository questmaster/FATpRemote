/*
 * Copyright (C) 2010 Daniel Jacobi
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

package de.questmaster.fatremote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * This class handles all network interaction. Therfore it is implemented as a singleton, as only one 
 * message at a time may be sent or received.
 * 
 * @author daniel
 *
 */
public class NetworkProxy {

	private static final String LOG_TAG = "NetworkProxy";

	private static volatile NetworkProxy singleton = null;
	private static FATDevice mFat = null;
	private Activity context = null;
	private BlockingQueue<FATRemoteEvent> mEventList = new ArrayBlockingQueue<FATRemoteEvent>(20, true);
	private Thread mSendingThread = null;
	
	/**
	 * Retrieve an instance of this object. If none exists, it is created.
	 * 
	 * @param c Context of the activity
	 * @return Instance of this class
	 */
	public static NetworkProxy getInstance(Activity c) {

		if (c == null) {
			throw new IllegalArgumentException("Context was null");
		}
			
		if (singleton == null) {
			
			// Initialize proxy
			NetworkProxy np = new NetworkProxy(); 
			
			singleton = np;
		}
		
		// read current Fat data
		AppSettings settings = new FatRemoteSettings.AppSettings();
		settings.readSettings(c);
		mFat = settings.getFat();

		singleton.context = c;

		return singleton;
	}

	/**
	 * Checks if Wifi is enabled and connected to a network.
	 * 
	 * @return true - if available, false - otherwise
	 */
	public boolean isWifiEnabled() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);

		if (!DebugHelper.ON_EMULATOR && !wifiManager.isWifiEnabled()) {
			return false;
		}

		return true;
	}

	/**
	 * Sends discovery message to the local Wifi. If a FAT+ device answers this 
	 * data is returned to the caller.
	 * 
	 * @return List of devices that answered to the discovery message
	 */
	private List<FATDevice> sendDiscoveryMsg() {
		Map<String, String> data = new HashMap<String, String>();
		List<DatagramPacket> answers = new ArrayList<DatagramPacket>();
		List<FATDevice> result = new ArrayList<FATDevice>();
		DatagramSocket ds = null;

		try {
			ds = new DatagramSocket();
			ds.setBroadcast(true);
			ds.setSoTimeout(3000);

			// Send Theater discovery
			DatagramPacket dp = new DatagramPacket("Search Venus".getBytes(), 12, InetAddress.getByName("255.255.255.255"), 10000);
			ds.send(dp);

			// Receive answers
			try {
				while (ds.isBound()) {
					byte[] buf = new byte[128];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);

					ds.receive(packet);

					answers.add(packet);
				}
			} catch (SocketTimeoutException e) {
			}

			// extract data
			String entry = null;
			for (DatagramPacket d : answers) {
						
				String adr = d.getAddress().getHostAddress();
				
				if (!data.containsKey(adr)) {
					data.put(adr, "");
				}
				
				entry = data.get(adr);
				String msgData = new String(d.getData(), 0, d.getLength());
				if (!entry.contains(msgData)) {
					entry += msgData;

					// message complete
					if (entry.contains("\n")) {
						entry = entry.trim();

						String name = entry.substring(entry.lastIndexOf(':') + 1);
						String ip = name.substring(name.indexOf(';') + 1).trim();
						name = name.substring(0, name.indexOf(';'));
						
						result.add(new FATDevice(name, ip, true));
					}
				}
				data.put(adr, entry);
			}

		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			if (ds != null) {
				ds.close();
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
	public List<FATDevice> discoverFAT() throws ConnectException {
		List<FATDevice> adr = null;

		// Check wifi availability
		if (isWifiEnabled()) {

			// get detected fat's
			adr = sendDiscoveryMsg();
		} else {
			throw new ConnectException(context.getResources().getString(R.string.app_err_enwifi));
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
				Log.e(LOG_TAG, "Event not in queue (keyId): " + event.getRemoteCode()[3], e);
			}
		
		if (mSendingThread == null) {
			mSendingThread = new Thread(new Runnable() {
				/** Executed by thread */
				public void run() {
					boolean goon = true;
					while (goon) {
						try {
							sendCode();
						} catch (final IOException e) {
							if (e.getCause() instanceof InterruptedException) {
								goon = false; // FIXME: close thread on closing of remote
								Log.i(LOG_TAG, "Thread interrupted.");
							} else {
								context.runOnUiThread(new Runnable() {
									/** Executed by thread */
									public void run() {
										Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
									}});
							}
						} 
	
						context.runOnUiThread(new Runnable() {
							/** Executed by thread */
							public void run() { // reset send image
								ImageView sending = (ImageView) context.findViewById(R.id.sendLED);
								if (sending != null) {
									sending.setImageResource(R.drawable.light_normal);
								}
						}});
					}
					Log.i(LOG_TAG, "Thread exited.");
				}
			});
			mSendingThread.start();
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
	
	private void sendCode() throws IOException {
		Socket cnx = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		short[] keyCode;

		assert mFat != null;
		
		// get event to send
			FATRemoteEvent event;
			try {
				event = mEventList.take();
				keyCode = event.getRemoteCode();
			} catch (InterruptedException e) {
				Log.e(LOG_TAG, e.getLocalizedMessage(), e);
				throw new IOException("", e); // empty message
			}
		
		try {
			// this makes sure wifi is up and running
			if (isWifiEnabled() || DebugHelper.ON_EMULATOR) {
				// Open Socket
				cnx = new Socket(mFat.getInetAddress(), mFat.getPort()); 

				// Open Streams
				bos = new BufferedOutputStream(cnx.getOutputStream(), 20);
				bis = new BufferedInputStream(cnx.getInputStream(), 4096);

				// send command
				Log.i(LOG_TAG, "Sending: " + keyCode[0] + ", " + keyCode[1] + ", " + keyCode[2] + ", " + keyCode[3] + ".");
				for (int i = 0; i < keyCode.length; i++) {
					bos.write(keyCode[i]);
				}
				bos.flush();

				if (DebugHelper.ON_EMULATOR) { // FIXME: include output of incoming data in regular release!
					writeIncomingData(bis);
				}
			} else {
				throw new IOException(context.getResources().getString(R.string.app_err_fatoffline));
			}
		} catch (UnknownHostException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(context.getResources().getString(R.string.app_err_wrongip), e);
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(context.getResources().getString(R.string.app_err_noconnection), e);
		} finally {
			try {
				// close streams
				if (bos != null) { bos.close(); }
				if (bis != null) { bis.close(); }
				if (cnx != null) { cnx.close(); }
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}

	private void writeIncomingData(BufferedInputStream bis) throws IOException {
		FileOutputStream fout = null;
		try {
		Thread.sleep(200);

		int read = 0, in;
		byte[] buf = new byte[4000];
		while ((in = bis.read(buf)) > 0) {
			read += in;
			if (fout == null) {
				Log.i(LOG_TAG, "Recived data from FAT: " + buf[0] + ", " + buf[1] + ", " + buf[2] + ", " + buf[3] + ". Buffersize: " + in);

				if (read > 4) {
					File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sqlite.out");
					if (f.createNewFile()) {
						fout = new FileOutputStream(f);

						// output and skip 4byte identifier
						fout.write(buf, 4, buf.length - 4);
					}
				}
			} else {
				fout.write(buf);
			}

		}
		if (read > 4 && fout != null) {
			fout.flush();
		}
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
	}
}
