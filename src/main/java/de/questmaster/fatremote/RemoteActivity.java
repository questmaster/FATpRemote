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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;
import de.questmaster.fatremote.fragments.RemoteFragment;
import de.questmaster.fatremote.network.NetworkProxy;

/**
 * Remote activity. This class is the activity shell of the remote fragment that serves 
 * the purpose of operating the FAT+ device.
 * 
 * @author daniel
 *
 */
public class RemoteActivity extends FragmentActivity {

	private AppSettings mSettings = new FatRemoteSettings.AppSettings();
	private static final int ANDROID_V11 = 11;
	private static final int ANDROID_V14 = 14;
	
	/**
	 * Called when the activity is first created. 
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) 
	 */
	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.remote_activity);

		// used in Honeycomb and up
		if (Build.VERSION.SDK_INT >= ANDROID_V11) {
			ActionBar ac = this.getActionBar();
			if (ac != null) {
				ac.setDisplayHomeAsUpEnabled(true);
				
				if (Build.VERSION.SDK_INT >= ANDROID_V14) {
					ac.setHomeButtonEnabled(true);
				}
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

	/**
	 * Called in the restart process.
	 * 
	 * @see android.app.Activity#onResume() 
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		setTitle(getString(R.string.app_title_remote, mSettings.getFat().getIp()));
	}
	
	public void onDebugButton(View v) {
		// short in1 = Short.decode(((TextView)findViewById(R.id.pos1)).getText().toString());
		// short in2 = Short.decode(((TextView)findViewById(R.id.pos2)).getText().toString());
		short keyCode = Short.decode(((TextView) this.findViewById(R.id.pos3)).getText().toString());
		short keyModifier = Short.decode(((TextView) this.findViewById(R.id.pos4)).getText().toString());

		// send keyCode
		NetworkProxy.getInstance(this).addRemoteEvent(new FATRemoteEvent(keyCode, keyModifier));
	}

}
