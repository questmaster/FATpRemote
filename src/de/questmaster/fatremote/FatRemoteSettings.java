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

import java.net.UnknownHostException;

import de.questmaster.fatremote.datastructures.FATDevice;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class FatRemoteSettings extends PreferenceActivity {

	
	private FatRemoteSettings.AppSettings mSettings = new FatRemoteSettings.AppSettings();
	private static final int ANDROID_V11 = 11;
	private static final int ANDROID_V14 = 14;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Read settings
		mSettings.readSettings(this);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fat_preferences);
		
		// used in Honeycomb and up
		if (Build.VERSION.SDK_INT >= ANDROID_V11) {
			ActionBar ac = this.getActionBar();
			ac.setDisplayHomeAsUpEnabled(true);
			
			if (Build.VERSION.SDK_INT >= ANDROID_V14) {
				ac.setHomeButtonEnabled(true);
			}
		}
	}

	/**
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish(); // simulate back button press
			break;
		default:
			// should not occur
			break;
		}			
		return super.onOptionsItemSelected(item);
	}

	public static class AppSettings
	{
		// default values
		private boolean mOverride = false;
		private boolean mTone = false;
		private boolean mVibrate = true;
		private FATDevice mFat = null;

		public FATDevice getFat() {
			return mFat;
		}


		public boolean isOverride() {
			return mOverride;
		}


		public boolean isTone() {
			return mTone;
		}


		public boolean isVibrate() {
			return mVibrate;
		}

		public void readSettings(Context pContext)
		{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
			Resources res = pContext.getResources();

			if (sharedPref != null) {
				mOverride = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_DEFAULT_BEHAVIOR), mOverride);
				mTone = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_TONE), mTone);
				mVibrate = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_VIBRATE), mVibrate);

				String fatIp = sharedPref.getString(res.getString(R.string.PREF_KEY_FAT), "");
				try {
					mFat = new FATDevice("", fatIp, false) ;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void setFat(Context pContext, FATDevice dev) {
			Editor sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext).edit();
			Resources res = pContext.getResources();
			
			mFat = dev;
			sharedPref.putString(res.getString(R.string.PREF_KEY_FAT), mFat.getIp());
			
			sharedPref.commit();
		}

	}
}
