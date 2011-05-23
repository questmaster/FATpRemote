package de.questmaster.fatremote;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectFATActivity extends ListActivity {

	// FIXME: introduce 'searching' dialog (ProgressDialog)

	private String[] ips = null;
	private Activity c = this;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectfat);

		// setup list
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// set click action
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// When clicked, show a toast with the TextView text
				setResult(Activity.RESULT_OK, new Intent().putExtra(StartActivity.INTENT_FAT_IP, ips[(int) id]));
				finish();
			}
		});

		// get data
		new Thread(new Runnable() {
			public void run() {
				ips = NetworkUtil.getInstance(c).discoverFAT();
				if (ips.length == 0) {
					c.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(c, R.string.app_err_noconnection, Toast.LENGTH_LONG).show();
						}
					});
					setResult(Activity.RESULT_CANCELED);
					finish();
				} else {
					c.runOnUiThread(new Runnable() {
						public void run() {
							setListAdapter(new ArrayAdapter<String>(c, R.layout.fatlist_item, R.id.textIP, ips));
						}
					});
				}
			}
		}).start();

	}

	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

}
