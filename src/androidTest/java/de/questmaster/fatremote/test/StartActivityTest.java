/**
 * 
 */
package de.questmaster.fatremote.test;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import de.questmaster.fatremote.StartActivity;
import de.questmaster.fatremote.testinfrastructure.IntentCatchingActivityUnitTestCase;

/**
 * @author daniel
 *
 */
public class StartActivityTest extends IntentCatchingActivityUnitTestCase<StartActivity> {

	private StartActivity mActivity;
	private ImageView mRemotePic;
	private Instrumentation mInstr;

	/**
	 * @param name
	 */
	public StartActivityTest(String name) {
		super(StartActivity.class);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		Assert.assertNotNull(mActivity);
		
		mRemotePic = (ImageView) mActivity.findViewById(de.questmaster.fatremote.R.id.remotePic);
		Assert.assertNotNull(mRemotePic);
		
		mInstr = this.getInstrumentation();
		Assert.assertNotNull(mInstr);
		
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testImageOnScreen() {
		final View origin = mActivity.getWindow().getDecorView();
		
		ViewAsserts.assertOnScreen(origin, mRemotePic);
	}
	
	public void testImageFullscreen() {
		final ScaleType expectedScaleType = ScaleType.FIT_XY;
		final int expectedFill = ViewGroup.LayoutParams.MATCH_PARENT;
		
		Assert.assertEquals(expectedScaleType, mRemotePic.getScaleType());
		Assert.assertEquals(expectedFill, mRemotePic.getLayoutParams().height);
		Assert.assertEquals(expectedFill, mRemotePic.getLayoutParams().width);
	}

    //@Ignore("not ready yet")
	public void testOnStartIntent() {
		// TODO: how to fix this?
		Intent expectedIntent = new Intent(mActivity.getBaseContext(), de.questmaster.fatremote.SelectFATActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		int expectedNum = 1;
		
		mInstr.callActivityOnStart(mActivity);
				
		Assert.assertEquals(expectedNum, mCaughtIntents.length);
		Assert.assertSame(expectedIntent, mCaughtIntents[0]);
	}
	
	public void testSetRequestedOrientation() {
		int expected = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		
		mInstr.callActivityOnResume(mActivity);
		
		Assert.assertEquals(expected, mActivity.getRequestedOrientation());
	}
}
