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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import de.questmaster.fatremote.datastructures.FATDevice;

public class NetworkProxy {

	private static NetworkProxy singleton = null;
	private FATDevice mFat = null;
	private Context context;

	public static NetworkProxy getInstance(Context c) {
		if (singleton == null) {
			singleton = new NetworkProxy();
			singleton.context = c;
		}

		return singleton;
	}

	/**
	 * @throws ConnectException
	 */
	public boolean isWifiEnabled() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled() && !StartActivity.ON_EMULATOR)
			return false;

		return true;
	}

	private Vector<FATDevice> sendDiscoveryMsg() {
		Hashtable<String, String> data = new Hashtable<String, String>();
		Vector<DatagramPacket> answers = new Vector<DatagramPacket>();
		Vector<FATDevice> result = new Vector<FATDevice>();
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
				// TODO: Nothing to do
			}

			// extract data TODO: what about multiple devices answering?! Use FATDevice
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

					if (entry.contains("\n")) {
						entry = entry.trim();
					}
				}
				data.put(adr, entry);
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ds != null)
				ds.close();
		}

		// parse received data
		for (String s : data.values()) {
			String name = s.substring(s.lastIndexOf(":") + 1);
			String ip = name.substring(name.indexOf(";") + 1).trim();
			name = name.substring(0, name.indexOf(";"));
			
			result.add(new FATDevice(name, ip, true));
		}
		
		return result;
	}

//	private Vector<String> pingBroadcast() {
//		Vector<String> ips = new Vector<String>();
//
//		try {
//			// exec broadcast ping
//			Runtime runtime = Runtime.getRuntime();
//			Process proc = runtime.exec("/system/bin/ping -c 3 -b 255.255.255.255");
//			proc.waitFor();
//
//			// retrieve results
//			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//			boolean done = false;
//
//			// parse output lines
//			while (br.ready() && !done) {
//				String line = br.readLine();
//
//				StringTokenizer st = new StringTokenizer(line, " ");
//				String ip = null;
//				int i = -1;
//
//				// check tokens 3 and 4
//				while (st.hasMoreTokens()) {
//					String token = st.nextToken();
//					i++;
//
//					if (i < 3) {
//						continue;
//					} else if (i == 3) {
//						ip = token.substring(0, token.length() - 1);
//					} else if (i == 4) {
//						if (token.contains("icmp_seq=1")) {
//							ips.add(ip);
//						} else if (token.contains("icmp_seq")) {
//							done = true;
//							break;
//						}
//					} else if (i > 4)
//						break;
//				}
//			}
//
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		return ips;
//	}

	public Vector<FATDevice> discoverFAT() throws ConnectException {
		Vector<FATDevice> adr = new Vector<FATDevice>();

		// Check wifi availability
		if (isWifiEnabled()) {

			// get detected fat's
			adr = sendDiscoveryMsg();
		} else {
			throw new ConnectException(context.getResources().getString(R.string.app_err_enwifi));
		}

		return adr;
	}

	public void sendCode(short[] keyCode) throws ConnectException { // TODO: change to private + use events to network proxy
		InetAddress mFATip = null;
		Socket cnx = null;

		if (mFat == null) {
			throw new IllegalStateException(); // TODO: add string with description
		}
		
		try {
			try {
				mFATip = InetAddress.getByName(mFat.getIp());
			} catch (UnknownHostException e) {
				e.printStackTrace();
				mFATip = null;
				throw new ConnectException(context.getResources().getString(R.string.app_err_wrongip));
			}

			// this makes sure wifi is up and running
			if (mFATip != null && (isWifiEnabled() || StartActivity.ON_EMULATOR)) {
				// Open Socket
				cnx = new Socket(mFATip, mFat.getPort()); // FIXME: hangs here on emulator if device down -> check if device alive before connecting

				// Open Streams
				BufferedOutputStream bos = new BufferedOutputStream(cnx.getOutputStream(), 20);
				BufferedInputStream bis = new BufferedInputStream(cnx.getInputStream(), 4096);

				// send command
				Log.i(StartActivity.LOG_TAG, "Sending: " + keyCode[0] + ", " + keyCode[1] + ", " + keyCode[2] + ", " + keyCode[3] + ".");
				for (int i = 0; i < keyCode.length; i++) {
					bos.write(keyCode[i]);
				}
				bos.flush();

				if (StartActivity.ON_EMULATOR) { // FIXME: include output of incoming data in regular release!
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					int read = 0, in;
					byte[] buf = new byte[4000];
					FileOutputStream fout = null;
					while ((in = bis.read(buf)) > 0) {
						read += in;
						if (fout == null) {
							Log.i(StartActivity.LOG_TAG, "Recived data from FAT: " + buf[0] + ", " + buf[1] + ", " + buf[2] + ", " + buf[3] + ". Buffersize: " + in);

							if (read > 4) {
								File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sqlite.out");
								f.createNewFile();
								fout = new FileOutputStream(f);

								// output and skip 4byte identifier
								fout.write(buf, 4, buf.length - 4);
							}
						} else
							fout.write(buf);

					}
					if (read > 4 && fout != null) {
						fout.flush();
						fout.close();
					}
				}

				// close streams
				bos.close();
				bis.close();
				cnx.close();
			} else
				throw new ConnectException(context.getResources().getString(R.string.app_err_fatoffline));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectException(context.getResources().getString(R.string.app_err_noconnection));
		}
	}

	/**
	 * @return the mFat
	 */
	public FATDevice getFat() {
		return mFat;
	}

	/**
	 * @param mFat the mFat to set
	 */
	public void setFat(FATDevice mFat) {
		this.mFat = mFat;
	}

}
