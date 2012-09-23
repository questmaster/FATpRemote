/**
 * 
 */
package de.questmaster.fatremote.fragments.test;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import de.questmaster.fatremote.R;
import de.questmaster.fatremote.SelectFATActivity;
import de.questmaster.fatremote.fragments.SelectFATFragment;

/**
 * @author daniel
 *
 */
public class SelectFATActivityTest extends ActivityInstrumentationTestCase2<SelectFATActivity> {

	private SelectFATActivity mActivity;
	private SelectFATFragment mFragment;
	private Instrumentation mInstr;
	private ImageView mBackground;
	private TextView mTitle;
	private ListView mList;
	private ImageView mLogo;
	private LinearLayout mListItem;
	

	/**
	 * @param name
	 */
	public SelectFATActivityTest(String name) {
		super(SelectFATActivity.class);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mActivity = getActivity();
		Assert.assertNotNull(mActivity);
		
		mFragment = (SelectFATFragment) mActivity.getSupportFragmentManager().findFragmentById(android.R.id.content);
		Assert.assertNotNull(mFragment);
				
		mInstr = this.getInstrumentation();
		Assert.assertNotNull(mInstr);		

		// Views
		mBackground = (ImageView) mActivity.findViewById(R.id.imageView1);
		Assert.assertNotNull(mBackground);
		mTitle = (TextView) mActivity.findViewById(R.id.title);
		Assert.assertNotNull(mTitle);
		mList = mFragment.getListView();
		Assert.assertNotNull(mList);
		mLogo = (ImageView) mActivity.findViewById(R.id.goflexlogo);
		Assert.assertNotNull(mLogo);
		mListItem = (LinearLayout) mActivity.findViewById(R.id.listItem);
		Assert.assertNotNull(mListItem);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInstanciateDetailsView() {
				
		Fragment frag = mActivity.getSupportFragmentManager().findFragmentById(android.R.id.content);
			
		Assert.assertNotNull(frag);
		Assert.assertTrue(frag instanceof SelectFATFragment);
	}
	
	public void testSetRequestedOrientation() {
		int expected = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			
		Assert.assertEquals(expected, mActivity.getRequestedOrientation());
	}
	
	public void testStartActivityOnFatSelection() {
		// TODO how to check this?
	}

	public void testHasFullScreenBackgroundImage() {
		ScaleType expectedType = ScaleType.FIT_XY;
		int expectedLayout = LayoutParams.MATCH_PARENT;
		
		Assert.assertEquals(expectedType, mBackground.getScaleType());
		Assert.assertEquals(expectedLayout, mBackground.getLayoutParams().height);
		Assert.assertEquals(expectedLayout, mBackground.getLayoutParams().width);
	}

	public void testHasBottomLogoImage() {
		ScaleType expectedType = ScaleType.FIT_CENTER;
		int expectedWidth = LayoutParams.MATCH_PARENT;
		int expectedHeight = LayoutParams.WRAP_CONTENT;
		
		Assert.assertEquals(expectedType, mLogo.getScaleType());
		Assert.assertEquals(expectedHeight, mLogo.getLayoutParams().height);
		Assert.assertEquals(expectedWidth, mLogo.getLayoutParams().width);
	}

	public void testHasListView() {
		Assert.assertNotNull(mList);
	}

	public void testHasTopTitleText() {
		int expectedWidth = LayoutParams.MATCH_PARENT;
		int expectedHeight = LayoutParams.WRAP_CONTENT;
		
		Assert.assertEquals(expectedHeight, mTitle.getLayoutParams().height);
		Assert.assertEquals(expectedWidth, mTitle.getLayoutParams().width);
	}

	public void testListItemContainsNameAndIP() {
		float expectedSize = 30.0F;
		int expectedNormal = Typeface.BOLD;
		
		TextView tn = (TextView) mListItem.findViewById(R.id.textName);
		TextView ti = (TextView) mListItem.findViewById(R.id.textIP);
		
		Assert.assertNotNull(tn);
		Assert.assertNotNull(ti);
		Assert.assertEquals(expectedSize, tn.getTextSize());
		Assert.assertEquals(expectedNormal, tn.getTypeface().getStyle());

		
	}
	
	public void testListItemContainsAutodetectIcon() {
		ImageView ia = (ImageView) mListItem.findViewById(R.id.iconAutodetect);
		
		Assert.assertNotNull(ia);
	}

}
