package de.questmaster.fatremote;

import java.net.ConnectException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import de.questmaster.fatremote.FatRemoteSettings.Settings;

public class StartActivity extends Activity {

	protected static final String INTENT_ALLFATS = "de.questmaster.fatremote.allfats";
	private static final String INTENT_FAT_IP = "de.questmaster.fatremote.fat_ip";

	private static final int INTENT_SELECT_FAT = 0;
	private static final int INTENT_SETTINGS_CHANGE = 1;

	protected static final boolean onEmulator = Build.PRODUCT.contains("sdk");

	private Settings mSettings = new FatRemoteSettings.Settings();
//	private String ips[];
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mSettings.ReadSettings(this);

	}

	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public void onPostResume() {
		super.onPostResume();
		
		mSettings.ReadSettings(this);

		// check if ip is known
		if (mSettings.m_sFatIP.equals("")) {
			//Show Settings
			Intent iSettings = new Intent();
			iSettings.setClass(this, FatRemoteSettings.class);
			startActivityForResult(iSettings, INTENT_SETTINGS_CHANGE);

			
//			// find possible ip's
//			ips = discoverFAT();
//
//			// show selectFAT activity
//			if (ips != null) {
//				Intent selectFAT = new Intent(Intent.ACTION_PICK);
//				selectFAT.putExtra(INTENT_ALLFATS, ips);
//
//				new SelectFATActivity().startActivityForResult(selectFAT, INTENT_SELECT_FAT);
//			}
//			
//			AlertDialog.Builder notification = new Builder(this);
//			notification.setIcon(android.R.drawable.ic_menu_manage);
//			notification.setTitle(R.string.dialog_noip_title);
//			notification.setMessage(R.string.dialog_noip_text);
//			notification.show();
		} else {
			// show remote
			Intent operateFAT = new Intent(Intent.ACTION_VIEW);
			operateFAT.setClass(this, RemoteActivity.class);
			operateFAT.putExtra(INTENT_FAT_IP, mSettings.m_sFatIP);
			startActivity(operateFAT);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case INTENT_SELECT_FAT: {
			if (resultCode == Activity.RESULT_OK) {
					Intent operateFAT = new Intent(Intent.ACTION_VIEW);
					operateFAT.putExtra(INTENT_FAT_IP, data.getStringExtra(INTENT_ALLFATS));
					// TODO: save ip in settings

					new RemoteActivity().startActivity(operateFAT);
			} else {
				System.exit(1);
			}

			break;
		}
		case INTENT_SETTINGS_CHANGE: {
			mSettings.ReadSettings(this);

			// TODO: check IP reachable
			
			Intent operateFAT = new Intent(Intent.ACTION_VIEW);
			operateFAT.setClass(this, RemoteActivity.class);
			operateFAT.putExtra(INTENT_FAT_IP, mSettings.m_sFatIP);
			startActivity(operateFAT);

			break;
		}
		}
	}


	private byte[] getLocalWifiIp() throws ConnectException {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);;
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		// ip is encoded -> decode
		byte ip1, ip2, ip3, ip4;
		ip1 = (byte) (ipAddress & 0x000000FF);
		ip2 = (byte) ((ipAddress & 0x0000FF00) >> 8);
		ip3 = (byte) ((ipAddress & 0x00FF0000) >> 16);
		ip4 = (byte) ((ipAddress & 0xFF000000) >> 24);

		if (ip1 == 0 && ip2 == 0 && ip3 == 0 && ip4 == 0 || !wifiManager.isWifiEnabled())
			throw new ConnectException(getResources().getString(R.string.app_err_wifioff));
		else
			return new byte[] { ip1, ip2, ip3, ip4 };

	}

//	private boolean pingIp(InetAddress ip) {
//		int exit = 1;
//
//		try {
//			Runtime runtime = Runtime.getRuntime();
//			Process proc = runtime.exec("ping " + ip.getHostAddress() + " -c 1"); // other
//																					// servers,
//																					// for
//																					// example
//			proc.waitFor();
//			exit = proc.exitValue();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return (exit == 0) ? true : false;
//	}
//
//	private String[] discoverFAT() {
//		Vector<String> adr = new Vector<String>();
//		byte[] testIp;
//
//		try {
//			testIp = getLocalWifiIp();
//		} catch (ConnectException e1) {
////			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
//			e1.printStackTrace();
//
//			if (onEmulator) {
//				return new String[] { "10.169.2.26" };
//			} else
//				return null;
//		}
//
//		for (int i = 1; i < 255; i++) {
//			testIp[3] = (byte) i;
//
//			try { // connect to IP
//				InetAddress ip = InetAddress.getByAddress(testIp);
//
//				// check for FAT availability
//				if (pingIp(ip)) {
//					adr.add(ip.getHostAddress());
//				}
//
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return adr.toArray(new String[] {});
//	}

}
