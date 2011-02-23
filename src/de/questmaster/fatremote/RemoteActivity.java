package de.questmaster.fatremote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.questmaster.fatremote.FatRemoteSettings.Settings;

public class RemoteActivity extends Activity {

	protected static final String INTENT_ALLFATS = "de.questmaster.fatremote.fats";

	private static final int INTENT_SELECT_FAT = 0;
	public static final int ON_SETTINGS_CHANGE = 1;

	private Settings mSettings = new FatRemoteSettings.Settings();
	private InetAddress mFATip = null;
	private int mPort = 9999;
	private Socket cnx = null;
	private AudioManager audioManager;
	private WifiManager wifiManager;

	private boolean onEmulator = Build.PRODUCT.contains("sdk");
	private boolean showDebugView = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.loadSoundEffects();

		readFATip();
		
		if (mSettings.m_sFatIP.equals("")) {
			AlertDialog.Builder notification = new Builder(this);
			notification.setIcon(android.R.drawable.ic_menu_manage);
			notification.setTitle(R.string.dialog_noip_title);
			notification.setMessage(R.string.dialog_noip_text);
			notification.show();
		}
	}

	/**
	 * Add menu items
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		// For testing purpose
		if (onEmulator) {
			MenuItem mi = menu.findItem(R.id.MENU_ITEM_DEBUG);
			mi.setVisible(true);
		}

		return true;
	}

	/**
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.MENU_ITEM_SELECTFAT:
			// // TODO: show FAT selection
			// String ips[] = discoverFAT();
			//
			// if (ips != null) {
			// Intent selectFAT = new Intent(Intent.ACTION_PICK);
			// selectFAT.putExtra(INTENT_ALLFATS, ips);
			//
			// new SelectFAT().startActivityForResult(selectFAT,
			// INTENT_SELECT_FAT);
			// }

			Intent iSettings = new Intent();
			iSettings.setClass(this, FatRemoteSettings.class);
			startActivityForResult(iSettings, ON_SETTINGS_CHANGE);

			break;
		case R.id.MENU_ITEM_DEBUG:
			if (showDebugView) {
				setContentView(R.layout.main);
			} else
				setContentView(R.layout.debug);
			break;
		default:
			// should not happen
		}
		return super.onOptionsItemSelected(item);
	}

	public void onResume() {
		super.onResume();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case INTENT_SELECT_FAT: {
			if (resultCode == Activity.RESULT_OK) {
				try {
					mFATip = InetAddress.getByName(data.getStringExtra(INTENT_ALLFATS));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			} else {
				mFATip = null;
			}

			break;
		}
		case ON_SETTINGS_CHANGE: {
			readFATip();
			break;
		}
		}
	}

	/**
	 * 
	 */
	private void readFATip() {
		mSettings.ReadSettings(this);

		try {
			mFATip = InetAddress.getByName(mSettings.m_sFatIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			mFATip = null;
			Toast.makeText(this, R.string.app_err_wrongip, Toast.LENGTH_LONG).show();
		}
		
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			Toast.makeText(this, R.string.app_err_enwifi, Toast.LENGTH_LONG).show();
			mFATip = null;
		}
	}

	private byte[] getLocalWifiIp() throws ConnectException {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		// ip is encoded -> decode
		byte ip1, ip2, ip3, ip4;
		ip1 = (byte) (ipAddress & 0x000000FF);
		ip2 = (byte) ((ipAddress & 0x0000FF00) >> 8);
		ip3 = (byte) ((ipAddress & 0x00FF0000) >> 16);
		ip4 = (byte) ((ipAddress & 0xFF000000) >> 24);
		
		if (onEmulator) {
			ip1 = 10;
			ip2 = (byte) 169;
			ip3 = 2;
			ip4 = 26;
		}

		if (ip1 == 0 && ip2 == 0 && ip3 == 0 && ip4 == 0 || !wifiManager.isWifiEnabled())
			throw new ConnectException(getResources().getString(R.string.app_err_wifioff));
		else
			return new byte[] { ip1, ip2, ip3, ip4 };

	}

	/*
	 * private String[] discoverFAT() { Vector<String> adr = new
	 * Vector<String>(); byte[] testIp;
	 * 
	 * try { testIp = getLocalWifiIp(); } catch (ConnectException e1) {
	 * Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
	 * e1.printStackTrace();
	 * 
	 * if (onEmulator) { testIp = new byte[] { 10, (byte) 169, 2, 26 }; } else
	 * return null; }
	 * 
	 * for (int i = 1; i < 255; i++) { testIp[3] = (byte) i;
	 * 
	 * try { // connect to IP InetAddress ip = InetAddress.getByAddress(testIp);
	 * 
	 * // TODO: How to check for FAT??? if (ip.isReachable(5)) {
	 * adr.add(ip.getHostAddress()); }
	 * 
	 * } catch (UnknownHostException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); } }
	 * 
	 * return (String[]) adr.toArray(); }
	 */

	public void onDebugButton(View v) {
		short in1 = Short.decode((String) ((TextView) findViewById(R.id.pos1)).getText());
		short in2 = Short.decode((String) ((TextView) findViewById(R.id.pos2)).getText());
		short in3 = Short.decode((String) ((TextView) findViewById(R.id.pos3)).getText());
		short in4 = Short.decode((String) ((TextView) findViewById(R.id.pos4)).getText());

		sendCode(new short[] { in1, in2, in3, in4 });
	}

	public void getKeyCode(View v) {
		byte keyCode = -1;

		// ring / vibrate
		if ((!mSettings.m_bOverride && audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) != 0) ||
				mSettings.m_bOverride && mSettings.m_bTone) {
			audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
		}

		if (mSettings.m_bVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(25);
		}

		// solve to code
		if (v instanceof Button) {
			Button bv = (Button) v;
			String t = (String) bv.getTag();

			if (t.equals("power"))
				keyCode = 0x38;
			else if (t.equals("home"))
				keyCode = 0x39;
			else if (t.equals("up"))
				keyCode = 0x40;
			else if (t.equals("left"))
				keyCode = 0x07;
			else if (t.equals("ok"))
				keyCode = 0x0E;
			else if (t.equals("right"))
				keyCode = 0x06;
			else if (t.equals("back"))
				keyCode = 0x1B;
			else if (t.equals("down"))
				keyCode = 0x41;
			else if (t.equals("info"))
				keyCode = 0x47;
			else if (t.equals("text"))
				Toast.makeText(this, R.string.app_err_buttonoffline, Toast.LENGTH_SHORT);
				//keyCode = 0x;
			else if (t.equals("rew"))
				keyCode = 0x52;
			else if (t.equals("menu"))
				keyCode = 0x1A;
			else if (t.equals("ff"))
				keyCode = 0x51;
			else if (t.equals("prev"))
				keyCode = 0x31;
			else if (t.equals("playpause"))
				keyCode = 0x4F;
			else if (t.equals("next"))
				keyCode = 0x30;
			else if (t.equals("volup"))
				keyCode = 0x32;
			else if (t.equals("stop"))
				keyCode = 0x54;
			else if (t.equals("zoomin"))
				keyCode = 0x35;
			else if (t.equals("voldown"))
				keyCode = 0x33;
			else if (t.equals("mute"))
				keyCode = 0x59;
			else if (t.equals("zoomout"))
				keyCode = 0x08;
		}

		sendCode(keyCode);
	}

	private void sendCode(short keyCode) {
		sendCode(new short[] { 0x48, 0x12, keyCode, 0x00 });
	}

	private void sendCode(short[] keyCode) {
		try {
//			NetworkInterface netInterface = NetworkInterface.getByInetAddress(InetAddress.getByAddress(getLocalWifiIp()));
			if (mFATip != null
					&& getLocalWifiIp() != null // this makes sure wifi is up and running
					/*&& mFATip.isReachable(netInterface, 5, 200)*/) {
				// Open Socket
				cnx = new Socket(mFATip, mPort);

				// Open Streams
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(cnx.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(cnx.getInputStream()));

				// send command
				char[] command = new char[keyCode.length];

				for (int i = 0; i < keyCode.length; i++) {
					command[i] = (char) keyCode[i];
				}

				bw.write(command);
				bw.flush();

				if (onEmulator) {
					char[] buf = new char[100];
					int read = br.read(buf);
					if (read > 0)
						Toast.makeText(this, "Recived data from FAT: \"" + String.copyValueOf(buf) + "\"", Toast.LENGTH_LONG);
				}

				// close streams
				bw.close();
				br.close();
				cnx.close();
			} else
				Toast.makeText(this, R.string.app_err_fatoffline, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, R.string.app_err_noconnection, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
