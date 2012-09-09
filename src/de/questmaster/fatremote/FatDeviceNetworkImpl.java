/**
 * 
 */
package de.questmaster.fatremote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
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

import android.util.Log;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * @author daniel
 *
 */
public class FatDeviceNetworkImpl implements FatDeviceNetwork {

	private final String LOG_TAG = getClass().getSimpleName();

	private FATDevice mFat = null;
	
	/**
	 * @param device
	 */
	public FatDeviceNetworkImpl(FATDevice device) {
		
		if (device == null) {
			throw new IllegalArgumentException("device is null.");
		}
		
		mFat = device;
	}

	@Override
	public List<FATDevice> getFatNetworkDevices() {
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

	@Override
	public FATRemoteEvent transmitRemoteEvent(FATRemoteEvent event) throws IOException {
		Socket cnx = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		FATRemoteEvent recEvent = null;

		// TODO: add payload in sending and receiving
		
		assert mFat != null;
				
		try {
				// Open Socket
				cnx = new Socket(mFat.getInetAddress(), mFat.getPort());
				cnx.setTcpNoDelay(true);

				// Open Streams
				bos = new BufferedOutputStream(cnx.getOutputStream(), 20);
				bis = new BufferedInputStream(cnx.getInputStream(), 4096);

				outgoingEvent(event, bos);

				if (cnx.isConnected()) {
					recEvent = incomingEvent(bis);
				}
		} catch (UnknownHostException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(Integer.toString(R.string.app_err_wrongip), e);
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			throw new IOException(Integer.toString(R.string.app_err_noconnection), e);
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
		
		return recEvent;
	}

	@Override
	public void setFatDevice(FATDevice device) {
		if (device != null) {
			mFat = device;
		}
	}

	@Override
	public FATDevice setFatDevice() {
		return mFat;
	}

	/**
	 * @param keyCode
	 * @param bos
	 * @throws IOException
	 */
	private void outgoingEvent(FATRemoteEvent event, BufferedOutputStream bos) throws IOException {
		short[] keyCode = event.getRemoteCode();
		
		// send command
		Log.i(LOG_TAG, "Sending: " + keyCode[0] + ", " + keyCode[1] + ", " + keyCode[2] + ", " + keyCode[3] + ".");
		for (int i = 0; i < keyCode.length; i++) {
			bos.write(keyCode[i]);
		}
		bos.flush();
	}

	private FATRemoteEvent incomingEvent(BufferedInputStream bis) throws IOException {
		FATRemoteEvent event = new FATRemoteEvent();
		byte[] code = new byte[4];
		byte[] buf = new byte[4000];
		
		try {
			Thread.sleep(20);

			if (bis.read(code) != 4) {
				Log.e(LOG_TAG, "Code not four byte long!");
			} else {
				Log.i(LOG_TAG, "Recived code from FAT: " + code[0] + ", " + code[1] + ", " + code[2] + ", " + code[3] + ".");
				event.setRemoteCode(code);
			}
			
			while (bis.read(buf) > 0) {

				// TODO: add payload code

			}
		} catch (InterruptedException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		
		return event;
	}

}
