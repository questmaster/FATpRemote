package de.questmaster.fatremote;

import de.questmaster.fatremote.FatRemoteSettings.Settings;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectFATActivity extends ListActivity {
	
	private String[] ips = null;
	private Activity c = this;
	private Settings mSettings = new FatRemoteSettings.Settings();
	private ProgressDialog mDialog;

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

				// save ip in settings
				mSettings.setFatIp(c, ips[(int) id]);
				
				// When clicked, show a toast with the TextView text
				setResult(Activity.RESULT_OK, new Intent().putExtra(StartActivity.INTENT_FAT_IP, ips[(int) id]));
				finish();
			}
		});

		getAvailableIps();
	}

	/**
	 * 
	 */
	private void getAvailableIps() {
		// show progress dialog
		mDialog = ProgressDialog.show(this, "", 
                getResources().getString(R.string.dialog_wait_searching), true);
		
		// get data
		new Thread(new Runnable() {
			public void run() {
				ips = NetworkUtil.getInstance(c).discoverFAT();
				if (ips.length == 0) {
					c.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(c, R.string.app_err_wifioff, Toast.LENGTH_LONG).show();
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
				
				// close progress dialog
				mDialog.dismiss();
			}
		}).start();
	}

	public void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Add menu items
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_select_fat, menu);

		return true;
	}

	/**
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.MENU_REFRESH:
			getAvailableIps();

			break;
		default:
			// should not happen
		}
		return super.onOptionsItemSelected(item);
	}

}
