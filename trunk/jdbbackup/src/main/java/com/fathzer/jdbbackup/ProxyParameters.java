package com.fathzer.jdbbackup;

public class ProxyParameters {
	String address;
	int port;
	String user;
	String pwd;
	
	public ProxyParameters() {
		this(null, -1);
	}
	
	public ProxyParameters(String address, int port) {
		this(address, port, null, null);
	}

	public ProxyParameters(String address, int port, String user, String pwd) {
		super();
		this.address = address;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
