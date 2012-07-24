package de.questmaster.fatremote.datastructures;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents a FAT+ device and its configuration data.
 * 
 * @author daniel
 *
 */
public class FATDevice {
	/**
	 * Default port on FAT+ for remote service.
	 */
	public static final int FAT_REMOTE_PORT = 9999;
	
	/**
	 * Name of this entry
	 */
	private String name = "";
	/**
	 * IP of device
	 */
	private String ip = null;
	/**
	 * Port of device
	 */
	private int port = FAT_REMOTE_PORT;
	/**
	 * Flag if the device was entered manually or discovered by this application.
	 */
	private boolean autoDetected = false;

	/**
	 * Create a new FAT+ device.
	 * 
	 * @param name Name of the device
	 * @param ip IP of the device
	 * @param auto Autodetected flag
	 */
	public FATDevice(String name, InetAddress ip, boolean auto) {
		this.name = name;
		this.ip = ip.getHostAddress();
		this.autoDetected = auto;
	}

	/**
	 * Create a new FAT+ device.
	 * 
	 * @param name Name of the device
	 * @param ip IP of the device
	 * @param auto Autodetected flag
	 */
	public FATDevice(String name, String ip, boolean auto) throws UnknownHostException {
		this.name = name;
		this.ip = InetAddress.getByName(ip).getHostAddress();
		this.autoDetected = auto;
	}

	/**
	 * Get name of the device.
	 * @return Device name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of the device.
	 * 
	 * @param name Device name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get IP as String of the device.
	 * 
	 * @return IP as String
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Set IP of the device as String.
	 * 
	 * @param ip Device IP
	 * @throws UnknownHostException
	 */
	public void setIp(String ip) throws UnknownHostException {
		if (InetAddress.getByName(ip) != null) {
			this.ip = ip;
		}
	}

	/**
	 * Get IP as InetAddress of the device.
	 * 
	 * @return IP as InetAdress
	 */
	public InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getByName(ip);
	}

	/**
	 * Set IP of the device as InetAddress.
	 * 
	 * @param ip Device IP
	 */
	public void setInetAdress(InetAddress ip) {
		this.ip = ip.getHostAddress();
	}

	/**
	 * Get port of device.
	 * 
	 * @return Device port
	 */
	public int getPort() {
		return port;
	}

	/** 
	 * Set port of device.
	 * 
	 * @param port Device port
	 */
	public void setPort(int port) {
		if (port > 0 && port <= 65535) {
			this.port = port;
		} else {
			this.port = 0;
		}
	}

	/**
	 * Check if device was automatically discovered.
	 * 
	 * @return true - Automatically discovered, false - else
	 */
	public boolean isAutoDetected() {
		return autoDetected;
	}

	/**
	 * Set if device was automatically discovered.
	 * 
	 * @param autoDetected Autodetected device
	 */
	public void setAutoDetected(boolean autoDetected) {
		this.autoDetected = autoDetected;
	}
}
