package de.questmaster.fatremote;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import de.questmaster.fatremote.FatRemoteSettings.Settings;

public class StartActivity extends Activity {

	protected static final String INTENT_ALLFATS = "de.questmaster.fatremote.allfats";
	protected static final String INTENT_FAT_IP = "de.questmaster.fatremote.fat_ip";

	private static final int INTENT_SELECT_FAT = 0;
	private static final int INTENT_SETTINGS_CHANGE = 1;

	protected static final String LOG_TAG = "FATremote";

	protected static final boolean onEmulator = Build.PRODUCT.contains("sdk");

	private Settings mSettings = new FatRemoteSettings.Settings();
	private static boolean discoveryFailed = false;

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

	public void onStart() {
		super.onStart();

		mSettings.ReadSettings(this);

		// check if ip is known
		if (mSettings.m_sFatIP.equals("")) {
			if (!discoveryFailed) { // WORKAROUND to skip starting again
				// show selectFAT activity
				Intent selectFAT = new Intent(Intent.ACTION_PICK);
				selectFAT.setClass(this, SelectFATActivity.class);
				startActivityForResult(selectFAT, INTENT_SELECT_FAT);
			}
		} else {
			// show remote
			Intent operateFAT = new Intent(Intent.ACTION_VIEW);
			operateFAT.setClass(this, RemoteActivity.class);
			operateFAT.putExtra(INTENT_FAT_IP, mSettings.m_sFatIP);
			startActivity(operateFAT);
			
			finish(); // Start activity done.
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSettings.ReadSettings(this);

		switch (requestCode) {
		case INTENT_SELECT_FAT: {
			if (resultCode == Activity.RESULT_CANCELED && mSettings.m_sFatIP.equals("")) {
				discoveryFailed = true;

				// Show Settings
				Intent iSettings = new Intent();
				iSettings.setClass(this, FatRemoteSettings.class);
				this.startActivityForResult(iSettings, INTENT_SETTINGS_CHANGE);

				break;
			}

			// break; // this is commented by intent!
		}
		case INTENT_SETTINGS_CHANGE: {
			discoveryFailed = false;

			// show remote
			if (!mSettings.m_sFatIP.equals("")) {
				Intent operateFAT = new Intent(Intent.ACTION_VIEW);
				operateFAT.setClass(this, RemoteActivity.class);
				operateFAT.putExtra(INTENT_FAT_IP, mSettings.m_sFatIP);
				this.startActivity(operateFAT);
			}				
			finish(); // Start activity done.

			break;
		}
		}
	}
}
