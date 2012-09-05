package de.questmaster.fatremote.test;

import java.net.UnknownHostException;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.ListView;
import de.questmaster.fatremote.FatRemoteSettings;
import de.questmaster.fatremote.FatRemoteSettings.AppSettings;
import de.questmaster.fatremote.datastructures.FATDevice;

public class FatRemoteSettingsTest extends ActivityInstrumentationTestCase2<FatRemoteSettings> {

	private FatRemoteSettings mActivity;
	private Instrumentation mInstr;
	private AppSettings mSettings = new FatRemoteSettings.AppSettings();

	public FatRemoteSettingsTest(String name) {
		super(FatRemoteSettings.class);
		setName(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
		Assert.assertNotNull(mActivity);
		
//		mRemotePic = (ImageView) mActivity.findViewById(de.questmaster.fatremote.R.id.remotePic);
//		Assert.assertNotNull(mRemotePic);
		
		mInstr = this.getInstrumentation();
		Assert.assertNotNull(mInstr);

		mSettings.readSettings(mActivity);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@UiThreadTest
	public void testListViewShown() {
		final View origin = mActivity.getWindow().getDecorView();
		
//		mInstr.callActivityOnCreate(mActivity, null);

		ViewAsserts.assertOnScreen(origin, mActivity.getListView());
	}

	@UiThreadTest
	public void testCreation() {
		int expected = 3;
		
		ListView prefs =  mActivity.getListView();
		
		mInstr.callActivityOnCreate(mActivity, null);

		Assert.assertEquals(expected, prefs.getCount());
	}

	public void testSelectVibrate() {
		boolean expected = true;
			
		Assert.assertEquals(expected, mSettings.isVibrate());
	}

	public void testSelectOverride() {
		boolean expected = false;
		
		Assert.assertEquals(expected, mSettings.isOverride());
	}

	public void testSelectTone() {
		boolean expected = true;
		
		Assert.assertEquals(expected, mSettings.isTone());
	}

	public void testSetReadDevice() {
		try {
			FATDevice expected = new FATDevice("Test", "1.2.3.4", false);
			
			mSettings.setFat(mActivity, expected);
			
			Assert.assertSame(expected, mSettings.getFat());
			
		} catch (UnknownHostException e) {
			Assert.fail("Exception occured : " + e.getLocalizedMessage());
		}
	}
}
