package de.questmaster.fatremote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
		setContentView(R.layout.remote);

		/* start audio */
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.loadSoundEffects();

		/* read IP */
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

//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case INTENT_SELECT_FAT: {
//			if (resultCode == Activity.RESULT_OK) {
//				try {
//					mFATip = InetAddress.getByName(data.getStringExtra(INTENT_ALLFATS));
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//				}
//			} else {
//				mFATip = null;
//			}

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

		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			Toast.makeText(this, R.string.app_err_enwifi, Toast.LENGTH_LONG).show();
		}
		
		try {
			mFATip = InetAddress.getByName(mSettings.m_sFatIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			mFATip = null;
			Toast.makeText(this, R.string.app_err_wrongip, Toast.LENGTH_LONG).show();
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

//		if (onEmulator) {
//			ip1 = 10;
//			ip2 = (byte) 169;
//			ip3 = 2;
//			ip4 = 26;
//		}

		if (ip1 == 0 && ip2 == 0 && ip3 == 0 && ip4 == 0 || !wifiManager.isWifiEnabled())
//			throw new ConnectException(getResources().getString(R.string.app_err_wifioff));
			return null;
		else
			return new byte[] { ip1, ip2, ip3, ip4 };
	}

	public void onDebugButton(View v) {
		short in1 = Short.decode(((TextView) findViewById(R.id.pos1)).getText().toString());
		short in2 = Short.decode(((TextView) findViewById(R.id.pos2)).getText().toString());
		short in3 = Short.decode(((TextView) findViewById(R.id.pos3)).getText().toString());
		short in4 = Short.decode(((TextView) findViewById(R.id.pos4)).getText().toString());

		sendCode(new short[] { in1, in2, in3, in4 });
	}

	public boolean onKeyDown (int keyCode, KeyEvent event) {
		short key = 0;
		int unicode = 0;
		
		unicode = event.getUnicodeChar();
		key = (short) unicode;
		Log.i("FATremote", "KeyEvent: "+key+", unicode: "+unicode);
		
		// Send Character
		if (key != 0) {
			sendCode(new short[] { 0x48, 0x12, 0xf9, key });
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}
	
	public void getKeyCode(View v) {
		short keyCode = -1;

		// ring / vibrate
		if ((!mSettings.m_bOverride && audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) != 0) || (mSettings.m_bOverride && mSettings.m_bTone)) {
			audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
		}

		if (mSettings.m_bVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(25);
		}

		// TODO: set send image

		// solve to code
		if (v instanceof ImageView) {
			ImageView bv = (ImageView) v;
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
			else if (t.equals("text")) {
				showKeyboard();
				return;
			} else if (t.equals("rew"))
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

		sendCode(new short[] { 0x48, 0x12, keyCode, 0x00 });
	}

	private void showKeyboard() {
		 InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 mgr.toggleSoftInput(0, 0);
	}

	private void sendCode(short[] keyCode) {
		try {
			// this makes sure wifi is up and running
			if ( mFATip != null && (getLocalWifiIp() != null || onEmulator) ) {
				// Open Socket
				cnx = new Socket(mFATip, mPort);

				// Open Streams
				BufferedOutputStream bos = new BufferedOutputStream(cnx.getOutputStream(), 20);
				BufferedInputStream bis = new BufferedInputStream(cnx.getInputStream(), 4096);

				// send command
				for (int i = 0; i < keyCode.length; i++) {
					bos.write(keyCode[i]);
				}
				bos.flush();

				if (onEmulator) {
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
							File f = new File("/mnt/sdcard/fatremote.out");
							f.createNewFile();
							fout = new FileOutputStream(f);
						}
						fout.write(buf);
						
					}
					if (read > 0) {
						fout.flush();
						fout.close();
						Toast.makeText(this, "Recived data from FAT: " + read + ".", Toast.LENGTH_LONG);
					}
				}

				// close streams
				bos.close();
				bis.close();
				cnx.close();
			} else
				Toast.makeText(this, R.string.app_err_fatoffline, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, R.string.app_err_noconnection, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

}
