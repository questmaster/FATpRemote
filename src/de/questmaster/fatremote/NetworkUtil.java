package de.questmaster.fatremote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class NetworkUtil {

	private static NetworkUtil singleton = null;
	private InetAddress mFATip = null;
	private int mPort = 9999;
	private Socket cnx = null;
	private Context context;

	public static NetworkUtil getInstance(Context c) {
		if (singleton == null) {
			singleton = new NetworkUtil();
			singleton.context = c;
		}
		
		return singleton;
	}
	
	/**
	 * @throws ConnectException
	 */
	public boolean isWifiEnabled() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
	
		if (!wifiManager.isWifiEnabled() && !StartActivity.onEmulator)
			return false;
		
		return true;
	}

	private Vector<String> pingBroadcast() {
		Vector<String> ips = new Vector<String>();
		
		try {
			// exec broadcast ping
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("/system/bin/ping -c 3 -b 255.255.255.255"); 
			proc.waitFor();
			
			// retrieve results
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			boolean done = false;
	
			// parse output lines
			while(br.ready() && !done) {
				String line = br.readLine();
				
				StringTokenizer st = new StringTokenizer(line, " ");
				String ip = null;
				int i = -1;

				// check tokens 3 and 4
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					i++;
					
					if (i < 3) {
						continue;
					} else if (i == 3) {
						ip = token.substring(0, token.length()-1);
					} else if (i == 4) {
						if (token.contains("icmp_seq=1")) {
							ips.add(ip);
						} else if (token.contains("icmp_seq")) {
							done = true;
							break;
						}
					} else if (i > 4)
						break;
				}
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		return ips;
	}

	public String[] discoverFAT() {
		Vector<String> adr = new Vector<String>();
	
		// Check wifi availability
		if (isWifiEnabled()) {
		
			// get computers
			adr = pingBroadcast();
		
	//		// TODO: check for FAT+ or other device
	//		for (String testIp : adr) {
	//	
	//			try {
	//				InetAddress ip = InetAddress.getByName(testIp);
	//	
	//				// TODO: send command 48 12 fc 00
	//				
	//				if (!"Venus") {
	//					adr.remove(testIp);
	//				}
	//	
	//			} catch (UnknownHostException e) {
	//				e.printStackTrace();
	//			}
	//		}
		}
		
		return adr.toArray(new String[] {});
	}

	public void sendCode(String ip, short[] keyCode) throws ConnectException {
		try {
			try {
				mFATip = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				mFATip = null;
				throw new ConnectException(context.getResources().getString(R.string.app_err_wrongip));
			}
			
			// this makes sure wifi is up and running
			if ( mFATip != null && (isWifiEnabled() || StartActivity.onEmulator) ) {
				// Open Socket
				cnx = new Socket(mFATip, mPort); // FIXME: hangs here on emulator if device down -> check if device alive before connecting

				// Open Streams
				BufferedOutputStream bos = new BufferedOutputStream(cnx.getOutputStream(), 20);
				BufferedInputStream bis = new BufferedInputStream(cnx.getInputStream(), 4096);

				// send command
				Log.i(StartActivity.LOG_TAG, "Sending: " + keyCode[0] + ", " + keyCode[1] + ", " + keyCode[2] + ", " + keyCode[3] + ".");
				for (int i = 0; i < keyCode.length; i++) {
					bos.write(keyCode[i]);
				}
				bos.flush();

				if (StartActivity.onEmulator) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					int read = 0, in;
					byte[] buf = new byte[4000];
					FileOutputStream fout = null;
					while((in = bis.read(buf)) > 0) {
						read += in;
						if (fout == null) {
							Log.i(StartActivity.LOG_TAG, "Recived data from FAT: " + buf[0] + ", " + buf[1] + ", " + buf[2] + ", " + buf[3] + ". Buffersize: " + in);
							
							if (read > 4) {
								File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sqlite.out");
								f.createNewFile();
								fout = new FileOutputStream(f);
	
								// output and skip 4byte identifier
								fout.write(buf, 4, buf.length-4);
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

	
}
