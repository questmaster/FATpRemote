package de.questmaster.fatremote;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.fragments.RemoteFragment;

public class RemoteActivity extends FragmentActivity {

	private AppSettings mSettings = new FatRemoteSettings.AppSettings();
	private static final int ANDROID_V11 = 11;
	private static final int ANDROID_V14 = 14;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.remote_activity);

		// used in Honeycomb and up
		if (Build.VERSION.SDK_INT >= ANDROID_V11) {
			ActionBar ac = this.getActionBar();
			ac.setDisplayHomeAsUpEnabled(true);
			
			if (Build.VERSION.SDK_INT >= ANDROID_V14) {
				ac.setHomeButtonEnabled(true);
			}
		}

		if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            RemoteFragment details = new RemoteFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
		
		mSettings.readSettings(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setTitle(getString(R.string.app_title_remote, mSettings.getFat().getIp()));
	}
}
