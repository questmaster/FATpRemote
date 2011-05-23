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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class FatRemoteSettings extends PreferenceActivity {

	
	private FatRemoteSettings.Settings mSettings = new FatRemoteSettings.Settings();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Read settings
		mSettings.ReadSettings(this);

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fat_preferences);
	}


	public static class Settings
	{
		// default values
		public String m_sFatIP = "";
		public boolean m_bOverride = false;
		public boolean m_bTone = true;
		public boolean m_bVibrate = true;


		public void ReadSettings(Context p_oContext)
		{
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(p_oContext);
			Resources res = p_oContext.getResources();

			if (sharedPref != null) {
				
				m_sFatIP = sharedPref.getString(res.getString(R.string.PREF_KEY_FAT_IP), m_sFatIP);

			}
		}
		
		public void setFatIp(Context p_oContext, String fat_ip) {
			Editor sharedPref = PreferenceManager.getDefaultSharedPreferences(p_oContext).edit();
			Resources res = p_oContext.getResources();
			
			m_sFatIP = fat_ip;
			sharedPref.putString(res.getString(R.string.PREF_KEY_FAT_IP), m_sFatIP);
			
			sharedPref.commit();
		}

	}
}
