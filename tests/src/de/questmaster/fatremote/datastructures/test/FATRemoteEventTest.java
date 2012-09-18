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

package de.questmaster.fatremote.datastructures.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import de.questmaster.fatremote.datastructures.FATRemoteEvent;

/**
 * @author daniel
 *
 */
public class FATRemoteEventTest extends AndroidTestCase {

	private FATRemoteEvent mRemoteEvent = null;
	
	private short expectedFirst = 0x48;
	private short expectedSecond = 0x12;
	private short expectedThird = 0x80;
	private short expectedFourth = 0xFF;
	private int expectedCodeLength = 4;
	private short[] expectedPayload = new short[] { 0, 1 , 2, 128, 255, 127};
	
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
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedThird, expectedFourth);
		Assert.assertNotNull(remoteEvent);
		Assert.assertNotNull(remoteEvent.getPayload());

		short[] code = remoteEvent.getCommandCode();
		Assert.assertEquals(expectedCodeLength, code.length);
		Assert.assertEquals(mRemoteEvent, remoteEvent);
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.datastructures.FATRemoteEvent#getCommandCode()}.
	 */
	public void testGetCommandCode() {
		short[] code = mRemoteEvent.getCommandCode();
		
		Assert.assertEquals(expectedCodeLength, code.length);
		Assert.assertEquals(expectedFirst, code[0]);
		Assert.assertEquals(expectedSecond, code[1]);
		Assert.assertEquals(expectedThird, code[2]);
		Assert.assertEquals(expectedFourth, code[3]);
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.datastructures.FATRemoteEvent#getPayload()}.
	 */
	public void testGetPayload() {
		short[] payload = mRemoteEvent.getPayload();
		
		Assert.assertNotNull(payload);
		Assert.assertEquals(expectedPayload.length, payload.length);
		for (int i = 0; i < payload.length; i++) {
			Assert.assertEquals(expectedPayload[i], payload[i]);
		}
	}

	public void testToString() {
		String expected = "{" + expectedFirst + "," + expectedSecond + "," + expectedThird + "," + expectedFourth + "}";
		
		assertEquals(expected, mRemoteEvent.toString());
	}
	
	public void testEquals() {
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedThird, expectedFourth, expectedPayload);
		
		assertEquals(mRemoteEvent, mRemoteEvent);
		assertEquals(mRemoteEvent, remoteEvent);
		assertEquals(remoteEvent, mRemoteEvent);
		
	    assertFalse(mRemoteEvent.equals(null));
	    assertFalse(mRemoteEvent.equals(new Object()));
	}

	public void testHashCode() {
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedThird, expectedFourth, expectedPayload);

		assertTrue(mRemoteEvent.hashCode() == mRemoteEvent.hashCode());
	    assertTrue(mRemoteEvent.hashCode() == remoteEvent.hashCode());
	}

	public void testSetCommandCode() {
		byte actual[] = {(byte) expectedFirst, (byte) expectedSecond, (byte) expectedThird, (byte) expectedFourth};
		short expected[] = { expectedFirst, expectedSecond, expectedThird, expectedFourth};
 		
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedFirst, expectedSecond, expectedPayload);
		
		// check value
		remoteEvent.setCommandCode(actual);
		
		short result[] = remoteEvent.getCommandCode();
		for (int i = 0; i < 4; i++)
			assertEquals(expected[i], result[i]);

		// check null
		remoteEvent.setCommandCode(null);
		
		result = remoteEvent.getCommandCode();
		for (int i = 0; i < 4; i++)
			assertEquals(expected[i], result[i]);

		// check empty
		remoteEvent.setCommandCode(new byte[0]);
		
		result = remoteEvent.getCommandCode();
		for (int i = 0; i < 4; i++)
			assertEquals(expected[i], result[i]);

	}

	public void testSetPayload() {
		byte actual[] = {0, 1, 2, -128, -1, 127};
		int expectedEmpty = 0;
 		
		FATRemoteEvent remoteEvent = new FATRemoteEvent(expectedFirst, expectedSecond, new short[0]);
		remoteEvent.setPayload(actual);
		
		// check set value
		short result[] = remoteEvent.getPayload();
		try {
		for (int i = 0; i < expectedPayload.length; i++)
			assertEquals(expectedPayload[i], result[i]);
		} catch (Exception e) {
			fail("Payload not equal!");
		}

		// check null
		remoteEvent.setPayload(null);
		
		result = remoteEvent.getPayload();
		try {
		for (int i = 0; i < expectedPayload.length; i++)
			assertEquals(expectedPayload[i], result[i]);
		} catch (Exception e) {
			fail("Payload not equal!");
		}

		// check empty
		remoteEvent.setPayload(new byte[0]);
		
		result = remoteEvent.getPayload();
		assertEquals(expectedEmpty, result.length);

	}

	public void testHasPayload() {
		boolean expectedWo = false;
		boolean expectedW = true;
		byte[] payload = {};
		
		// check value
		Assert.assertEquals(expectedW, mRemoteEvent.hasPayload());
		
		// check null
		mRemoteEvent.setPayload(null);

		Assert.assertEquals(expectedW, mRemoteEvent.hasPayload());

		// check empty
		mRemoteEvent.setPayload(payload);
		
		Assert.assertEquals(expectedWo, mRemoteEvent.hasPayload());
		
	}
}

	