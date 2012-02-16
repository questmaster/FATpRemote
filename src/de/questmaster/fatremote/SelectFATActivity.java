package de.questmaster.fatremote;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.questmaster.fatremote.datastructures.FATDevice;
import de.questmaster.fatremote.fragments.SelectFATFragment;


public class SelectFATActivity extends FragmentActivity implements SelectFATFragment.FATSelectedListener {
		
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.selectfat_activity);

		if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            SelectFATFragment details = new SelectFATFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }

	}


	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onFATSelected(FATDevice dev) {
		NetworkProxy.getInstance(this).setCurrentFat(dev);

		// When clicked, show a toast with the TextView text
		Intent operateFAT = new Intent(this, RemoteActivity.class);
		operateFAT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(operateFAT);

		finish();
	}


}
