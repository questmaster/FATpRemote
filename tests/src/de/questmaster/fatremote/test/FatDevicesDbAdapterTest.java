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

package de.questmaster.fatremote.test;

import android.test.AndroidTestCase;
import de.questmaster.fatremote.FatDevicesDbAdapter;

/**
 * @author daniel
 *
 */
public class FatDevicesDbAdapterTest extends AndroidTestCase {

	FatDevicesDbAdapter mTestObject = null;
	
	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mTestObject = new FatDevicesDbAdapter(mContext);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#FatDevicesDbAdapter(android.content.Context)}.
	 */
	public final void testFatDevicesDbAdapter() {
		assertNotNull(mTestObject);
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#open()}.
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#isOpen()}.
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#close()}.
	 */
	public final void testOpenIsOpenClose() {
		assertFalse(mTestObject.isOpen());

		mTestObject.open();

		assertTrue(mTestObject.isOpen());
		
		mTestObject.close();
		
		assertFalse(mTestObject.isOpen());
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#createFatDevice(java.lang.String, java.lang.String, int, boolean)}.
	 */
	public final void testCreateFatDevice() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#deleteFatDevice(long)}.
	 */
	public final void testDeleteFatDevice() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#deleteAllFatDevices()}.
	 */
	public final void testDeleteAllFatDevices() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#fetchAllFatDevices()}.
	 */
	public final void testFetchAllFatDevices() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#fetchFatDeviceTyp(long)}.
	 */
	public final void testFetchFatDeviceTyp() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#fetchFatDeviceId(java.lang.String)}.
	 */
	public final void testFetchFatDeviceId() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#getAllFatDevicesCount()}.
	 */
	public final void testGetAllFatDevicesCount() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link de.questmaster.fatremote.FatDevicesDbAdapter#updateFatDevice(long, java.lang.String, java.lang.String, int, boolean)}.
	 */
	public final void testUpdateFatDevice() {
		fail("Not yet implemented"); // TODO
	}

}
