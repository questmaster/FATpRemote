package de.questmaster.fatremote.datastructures.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.Assert;

import de.questmaster.fatremote.datastructures.FATDevice;
import android.test.AndroidTestCase;

public class FATDeviceTest extends AndroidTestCase {

	private String expectedAdress = "10.10.10.10";
	private String expectedName = "Name";
	private boolean expectedAuto = false;
	
	private FATDevice mDev;

	public FATDeviceTest() {
		super();
	}
	
	/**
	 * Test setup.
	 */
	protected void setUp() {
		mDev = new FATDevice(expectedName, expectedAdress, expectedAuto);
	}
	
//	/**
//	 * Test clean up.
//	 */
//	protected void tearDown() {
//		
//	}

	/**
	 * Check for correct setup.
	 */
	public void testAndroidTestCaseSetupProperly() {
		Assert.assertNotNull(mDev);
		Assert.assertEquals(FATDevice.FAT_REMOTE_PORT, 9999);
		
		Assert.assertEquals(expectedName, mDev.getName());
		Assert.assertEquals(expectedAdress, mDev.getIp());
		Assert.assertEquals(expectedAuto, mDev.isAutoDetected());
		try {
			Assert.assertEquals(expectedAdress, mDev.getInetAddress().getHostAddress());
		} catch (UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testFATDeviceConstructor() {
		try {
			FATDevice dev = new FATDevice(expectedName, InetAddress.getByName(expectedAdress), expectedAuto);
			Assert.assertEquals(expectedName, dev.getName());
			Assert.assertEquals(expectedAdress, dev.getInetAddress().getHostAddress());
			Assert.assertEquals(expectedAdress, dev.getIp());
			Assert.assertEquals(expectedAuto, dev.isAutoDetected());
		
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
			Assert.assertEquals(expectedName, dev.getName());
			Assert.assertEquals(expectedAdress, dev.getInetAddress().getHostAddress());
			Assert.assertEquals(expectedAdress, dev.getIp());
			Assert.assertEquals(expectedAuto, dev.isAutoDetected());

		} catch (UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testGetName() {
		Assert.assertEquals(expectedName, mDev.getName());
	}

	public void testSetName() {
		String expectedNewName = "NewName";
		
		FATDevice dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		dev.setName(expectedNewName);
		
		Assert.assertEquals(expectedNewName, dev.getName());
	}

	public void testGetIp() {
		Assert.assertEquals(expectedAdress, mDev.getIp());
	}

	public void testSetIp() {
		String expectedNewIP = "10.10.10.11";
		
		FATDevice dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		dev.setIp(expectedNewIP);
		
		Assert.assertEquals(expectedNewIP, dev.getIp());
	}

	public void testGetInetAddress() {
		try {
			Assert.assertEquals(expectedAdress, mDev.getInetAddress().getHostAddress());
		} catch (UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testSetInetAdress() {
		String expectedNewIP = "10.10.10.11";
		
		try {
			FATDevice dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
			dev.setInetAdress(InetAddress.getByName(expectedNewIP));
		
			Assert.assertEquals(expectedNewIP, dev.getInetAddress().getHostAddress());
		} catch (UnknownHostException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testGetPort() {
		Assert.assertEquals(FATDevice.FAT_REMOTE_PORT, mDev.getPort());
	}

	public void testSetPort() {
		int expectedNewPort = 666;
		
		FATDevice dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		dev.setPort(expectedNewPort);
		
		Assert.assertEquals(expectedNewPort, dev.getPort());
	}

	public void testIsAutoDetected() {
		Assert.assertEquals(expectedAuto, mDev.isAutoDetected());
	}

	public void testSetAutoDetected() {
		boolean expectedNewAuto = true;
		
		FATDevice dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		dev.setAutoDetected(expectedNewAuto);
		
		Assert.assertEquals(expectedNewAuto, dev.isAutoDetected());
	}
}