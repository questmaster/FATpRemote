/**
 * 
 */
package de.questmaster.fatremote.datastructures.test;

import junit.framework.Assert;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;
import android.test.AndroidTestCase;

/**
 * @author daniel
 *
 */
public class FATRemoteEventTest extends AndroidTestCase {

	private FATRemoteEvent mRemoteEvent = null;
	
	private short expectedFirst = 0x48;
	private short expectedSecond = 0x12;
	private short expectedThird = 3;
	private short expectedFourth = 4;
	private int expectedCodeLength = 4;
	private short[] expectedPayload = new short[] { 0, 1 , 2, 3, 4, 5};
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mRemoteEvent = new FATRemoteEvent(expectedThird, expectedFourth, expectedPayload);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		
		mRemoteEvent = null;
	}

	/**
	 * Check for correct setup.
	 */
	public void testAndroidTestCaseSetupProperly() {
		Assert.assertNotNull(mRemoteEvent);
	}
	
	/**
	 * Test method for {@link de.questmaster.fatremote.datastructures.FATRemoteEvent#FATRemoteEvent(short, short)}.
	 */
	public void testFATRemoteEvent() {
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedThird, expectedFourth, expectedPayload);
		Assert.assertNotNull(remoteEvent);
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.datastructures.FATRemoteEvent#getRemoteCode()}.
	 */
	public void testGetRemoteCode() {
		short[] code = mRemoteEvent.getRemoteCode();
		
		Assert.assertEquals(expectedCodeLength, code.length);
		Assert.assertEquals(expectedFirst, code[0]);
		Assert.assertEquals(expectedSecond, code[1]);
		Assert.assertEquals(expectedThird, code[2]);
		Assert.assertEquals(expectedFourth, code[3]);
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.datastructures.FATRemoteEvent#getCodePayload()}.
	 */
	public void testGetCodePayload() {
		short[] payload = mRemoteEvent.getCodePayload();
		
		Assert.assertNotNull(payload);
		Assert.assertEquals(expectedPayload.length, payload.length);
		for (int i = 0; i < payload.length; i++) {
			Assert.assertEquals(expectedPayload[i], payload[i]);
		}
	}

}
