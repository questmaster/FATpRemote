package de.questmaster.fatremote;

import java.net.ConnectException;
import java.util.Vector;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;
import de.questmaster.fatremote.datastructures.FATDevice;

public class SelectFATActivity extends ExpandableListActivity {

	public class CustomCursorTreeAdapter extends SimpleCursorTreeAdapter {

		public CustomCursorTreeAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			Cursor cur = null;

			if (groupCursor.getCount() > 0) {
				boolean autodetected = groupCursor.getInt(groupCursor.getColumnIndex(FatDevicesDbAdapter.KEY_AUTODETECTED)) == 1;

				cur = mDbHelper.fetchFatDeviceOfGroupDetection(autodetected);
				startManagingCursor(cur);
			}

			return cur;
		}
		
	}
	
	private Activity c = this;
	private ProgressDialog mDialog;
	private FatDevicesDbAdapter mDbHelper;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectfat);

		// Init Database
		mDbHelper = new FatDevicesDbAdapter(this);
		try {
			mDbHelper.open();
		} catch (SQLiteException e) {
			// TODO: fixme
			e.printStackTrace();
		}

		// setup list
		initExpandableListView();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		// save fat as active
		FATDevice fd = mDbHelper.fetchFatDeviceTyp(id);
		NetworkProxy.getInstance(c).setFat(fd);

		// When clicked, show a toast with the TextView text
		
		Intent operateFAT = new Intent(this, RemoteActivity.class);
		operateFAT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(operateFAT);

		finish();
		
		return true;
	}

	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	protected void onRestart() {
		super.onRestart();
		if (!mDbHelper.isOpen()) {
			mDbHelper.open();
		}
	}

	protected void onPause() {
		super.onPause();
		if (mDbHelper.isOpen()) {
			mDbHelper.close();
		}
	}

	/**
	 * 
	 */
	private void getAvailableIps() {

		// show progress dialog
		mDialog = ProgressDialog.show(this, "", getResources().getString(R.string.dialog_wait_searching), true);

		// get data
		new Thread(new Runnable() {
			public void run() {
				Vector<FATDevice> dev = null;

				// grab available ip's
				try {
					dev = NetworkProxy.getInstance(c).discoverFAT();

					// enter dev's into database
					for (FATDevice f : dev) {
						long rowId = mDbHelper.fetchFatDeviceId(f.getIp());
						if (rowId < 0) {
							mDbHelper.createFatDevice(f.getName(), f.getIp(), f.getPort(), f.isAutoDetected());
						} else {
							mDbHelper.updateFatDevice(rowId, f.getName(), f.getIp(), f.getPort(), f.isAutoDetected());
						}
					}

					// // resolve host names
					// try {
					// for (int i = 0; i < ips.length; i++) {
					// ipnames[i] = InetAddress.getByName(ips[i]).getHostName()
					// + " (" + ips[i] + ")";
					// }
					// } catch (UnknownHostException e) {
					// e.printStackTrace();
					// }

					if (dev.size() == 0) { // TODO: This may not be correct if manual dev are entered
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
								initExpandableListView();
							}
						});
					}
				} catch (ConnectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// close progress dialog
				mDialog.dismiss();
			}
		}).start();
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
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onGroupCollapse(int groupPosition) {
		// keep the Groups expanded
		getExpandableListView().expandGroup(groupPosition);
	}

	/**
	 * 
	 */
	private void initExpandableListView() {
		// Get all of the notes from the database and create the item list
		Cursor cursor = mDbHelper.fetchAllFatDevices();
		startManagingCursor(cursor);

		CustomCursorTreeAdapter fatDevices;

		String[] groupFrom = new String[] { FatDevicesDbAdapter.KEY_AUTODETECTED };
		int[] groupTo = new int[] { R.id.textCategory };

		String[] childFrom = new String[] { FatDevicesDbAdapter.KEY_NAME, FatDevicesDbAdapter.KEY_IP };
		int[] childTo = new int[] { R.id.textName, R.id.textIP };

		fatDevices = new CustomCursorTreeAdapter(c, cursor, R.layout.simple_expandable_list_item_1, groupFrom, groupTo, R.layout.simple_expandable_list_item_2, childFrom,
				childTo);

		setListAdapter(fatDevices);
		
		// expand all items
		Cursor cur = mDbHelper.fetchGroupsOfDetection();
		startManagingCursor(cur);
		for (int i = 0; i < cur.getCount(); i++) {
			getExpandableListView().expandGroup(i);
		}

	}

}
