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
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import java.net.UnknownHostException;

import de.questmaster.fatremote.datastructures.FATDevice;

/**
 * Settings activity to store the user settings and last selected FAT+ device. 
 * 
 * @author daniel
 *
 */
public class FatRemoteSettings extends PreferenceActivity {

	private static final String LOG_TAG = "FatRemoteSettings";
    private static final int ANDROID_V11 = 11;
    private static final int ANDROID_V14 = 14;
    private FatRemoteSettings.AppSettings mSettings = new FatRemoteSettings.AppSettings();
	
	@TargetApi(14)
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

	/**
	 * This class stores the settings to be available to the application.
	 * 
	 * @author daniel
	 *
	 */
	public static class AppSettings
	{
		// default values
		private boolean mOverride = false;
		private boolean mTone = false;
		private boolean mVibrate = true;
		private FATDevice mFat = null;

		/**
		 * Get FAT+ device selected last time.
		 * 
		 * @return Last FATDevice
		 */
		public FATDevice getFat() {
			return mFat;
		}

		/**
		 * Checks if override-mode was selected in settings.
		 * 
		 * @return true - is selected, false - otherwise.
		 */
		public boolean isOverride() {
			return mOverride;
		}


		/**
		 * Checks if sound option was selected in settings.
		 * 
		 * @return true - is selected, false - otherwise.
		 */
		public boolean isTone() {
			return mTone;
		}


		/**
		 * Checks if vibration was selected in settings.
		 * 
		 * @return true - is selected, false - otherwise.
		 */
		public boolean isVibrate() {
			return mVibrate;
		}

		/**
		 * Read the settings into the local variables.
		 * 
		 * @param pContext Context of the activity
		 */
		public void readSettings(Context pContext)
		{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext);
			Resources res = pContext.getResources();
			
			FatDevicesDbAdapter mDbHelper = new FatDevicesDbAdapter(pContext);
			try {
				mDbHelper.open();
			} catch (SQLiteException e) {
				Log.e(LOG_TAG, "Database failed to initialize.", e);
			}


			if (sharedPref != null) {
				mOverride = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_DEFAULT_BEHAVIOR), mOverride);
				mTone = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_TONE), mTone);
				mVibrate = sharedPref.getBoolean(res.getString(R.string.PREF_KEY_VIBRATE), mVibrate);

				String fatIp = sharedPref.getString(res.getString(R.string.PREF_KEY_FAT), "");
				mFat = mDbHelper.fetchFatDeviceTyp(mDbHelper.fetchFatDeviceId(fatIp));
				
				// Provide empty default, if last device missing
				if (mFat == null) {
					try {
						mFat = new FATDevice("", "0.0.0.0", false);
					} catch (UnknownHostException e) {
						Log.e(LOG_TAG, e.getMessage(), e);
					}
				}
			}
			mDbHelper.close();
		}
		
		/**
		 * Sets the last selected FAT+ device.
		 * 
		 * @param pContext Context of the activity
		 * @param dev Device to be set
		 */
		public void setFat(Context pContext, FATDevice dev) {
			Editor sharedPref = PreferenceManager.getDefaultSharedPreferences(pContext).edit();
			Resources res = pContext.getResources();
			
			mFat = dev;
			sharedPref.putString(res.getString(R.string.PREF_KEY_FAT), mFat.getIp());

            sharedPref.apply();
        }

	}
}
