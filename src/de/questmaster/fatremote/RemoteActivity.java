package de.questmaster.fatremote;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.questmaster.fatremote.fragments.RemoteFragment;

public class RemoteActivity extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.remote_activity);

		if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            RemoteFragment details = new RemoteFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
	}


}
