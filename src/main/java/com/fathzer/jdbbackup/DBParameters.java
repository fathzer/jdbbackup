package com.fathzer.jdbbackup;

public class DBParameters {
	private String host;
	private String port;
	private String user;
	private String pwd;
	private String mySQLPath;

	public DBParameters() {
		this.host = "127.0.0.1";
		this.port = "3306";
		this.user = "root";
		this.pwd = "";
		this.mySQLPath = "";
	}

//	public String getMySQLPath() {
//		return mySQLPath;
//	}
//
//	public void setMySQLPath(String mySQLPath) {
//		this.mySQLPath = mySQLPath;
//	}

	public String getHost() {
		return host;
	}

	public void setHost(String dbHost) {
		this.host = dbHost;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String dbPort) {
		this.port = dbPort;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String dbUser) {
		this.user = dbUser;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String dbPwd) {
		this.pwd = dbPwd;
	}
}
