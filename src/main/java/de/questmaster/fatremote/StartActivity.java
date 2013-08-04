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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * Main class. This class initiates the whole application process.
 * 
 * @author daniel
 *
 */
public class StartActivity extends Activity {

	/**
	 * Called when the activity is first created. 
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	
	/**
	 * Called in the restart process.
	 * 
	 * @see android.app.Activity#onResume() 
	 */
	@Override
	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Called in the start process.
	 * 
	 * @see android.app.Activity#onStart() 
	 */
	@Override
	public void onStart() {
		super.onStart();

		// show selectFAT activity
		Intent selectFAT = new Intent(this, SelectFATActivity.class);
		selectFAT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(selectFAT);
			
		finish(); // Start activity done.
	}

}
