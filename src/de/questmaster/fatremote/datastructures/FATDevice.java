package de.questmaster.fatremote.datastructures;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FATDevice {
	public static final int FAT_REMOTE_PORT = 9999;
	
	private String name = "";
	private String ip = null;
	private int port = FAT_REMOTE_PORT;
	private boolean autoDetected = false;

	public FATDevice(String name, InetAddress ip, boolean auto) {
		this.name = name;
		this.ip = ip.getHostAddress();
		this.autoDetected = auto;
	}

	public FATDevice(String name, String ip, boolean auto) throws UnknownHostException {
		this.name = name;
		this.ip = InetAddress.getByName(ip).getHostAddress();
		this.autoDetected = auto;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) throws UnknownHostException {
		if (InetAddress.getByName(ip) != null) {
			this.ip = ip;
		}
	}

	public InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getByName(ip);
	}

	public void setInetAdress(InetAddress ip) {
		this.ip = ip.getHostAddress();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (port > 0 && port <= 65535) {
			this.port = port;
		} else {
			this.port = 0;
		}
	}

	public boolean isAutoDetected() {
		return autoDetected;
	}

	public void setAutoDetected(boolean autoDetected) {
		this.autoDetected = autoDetected;
	}
}
