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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.fragments.SelectFATFragment;

/**
 * This class provides the activity shell for the SelectFATFragment. It provides the selection dialog for FAT 
 * devices and the possibility to automatically discover devices or enter them manually.
 * 
 * @author daniel
 *
 */
public class SelectFATActivity extends FragmentActivity implements SelectFATFragment.FATSelectedListener {
		
	private AppSettings mSettings = new FatRemoteSettings.AppSettings();
	
	/**
	 * Called when the activity is first created. 
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            SelectFATFragment details = new SelectFATFragment();
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
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Implements FATSelectedListener of SelectFATFragment.
	 * 
	 * @see de.questmaster.fatremote.fragments.SelectFATFragment.FATSelectedListener.onFATSelected()
	 */
	public void onFATSelected(FATDevice dev) {
		mSettings.setFat(this, dev);

		// When clicked, show a toast with the TextView text
		Intent operateFAT = new Intent(this, RemoteActivity.class);
		operateFAT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(operateFAT);

		finish();
	}


}
