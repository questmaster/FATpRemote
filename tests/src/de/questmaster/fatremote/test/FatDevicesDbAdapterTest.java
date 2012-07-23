/**
 * 
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
