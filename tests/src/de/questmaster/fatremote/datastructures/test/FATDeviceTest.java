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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

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
		try {
			mDev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}
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
		
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		dev.setName(expectedNewName);
		
		Assert.assertEquals(expectedNewName, dev.getName());
	}

	public void testGetIp() {
		Assert.assertEquals(expectedAdress, mDev.getIp());
	}

	public void testSetIp() {
		String expectedNewIP = "10.10.10.11";
		String newBullshitIP = "bullshit-IP";
		boolean exceptionTriggered = false;
		
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e2) {
			Assert.fail(e2.getLocalizedMessage());
		}
		
		// set new IP
		try {
			dev.setIp(expectedNewIP);
		} catch (UnknownHostException e1) {
			Assert.fail(e1.getLocalizedMessage());
		}
		Assert.assertEquals(expectedNewIP, dev.getIp());
		
		// set new bullshit IP
		try {
			dev.setIp(newBullshitIP);
		} catch (UnknownHostException e) {
			exceptionTriggered = true;
		}
		Assert.assertTrue(exceptionTriggered);
				
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
		Hashtable<Integer, Integer> expectedNewPorts = new Hashtable<Integer, Integer>();
		expectedNewPorts.put(-1, 0);
		expectedNewPorts.put(0, 0);
		expectedNewPorts.put(65535, 65535);
		expectedNewPorts.put(65536, 0);		
		
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
		for (int port : expectedNewPorts.keySet() ) {
			dev.setPort(port);
			Assert.assertEquals(expectedNewPorts.get(port).intValue(), dev.getPort());
		}
	}

	public void testIsAutoDetected() {
		Assert.assertEquals(expectedAuto, mDev.isAutoDetected());
	}

	public void testSetAutoDetected() {
		boolean expectedNewAuto = true;
		
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		dev.setAutoDetected(expectedNewAuto);
		
		Assert.assertEquals(expectedNewAuto, dev.isAutoDetected());
	}
	
	public void testFATRemotePort() {
		int expectedPort = 9999;
		
		assertEquals(expectedPort, FATDevice.FAT_REMOTE_PORT);
	}

	public void testToString() {
		String expected = expectedName + ";" + expectedAdress + ":" + FATDevice.FAT_REMOTE_PORT;
		
		assertEquals(expected, mDev.toString());
	}
	
	public void testEquals() {
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
		assertEquals(mDev, mDev);
		assertEquals(mDev, dev);
		assertEquals(dev, mDev);
		
	    assertFalse(mDev.equals(null));
	    assertFalse(mDev.equals(new Object()));
	}

	public void testHashCode() {
		FATDevice dev = null;
		try {
			dev = new FATDevice(expectedName, expectedAdress, expectedAuto);
		} catch (UnknownHostException e) {
			Assert.fail(e.getLocalizedMessage());
		}

		assertTrue(mDev.hashCode() == mDev.hashCode());
	    assertTrue(mDev.hashCode() == dev.hashCode());
	}
}
