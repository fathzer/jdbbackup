package com.fathzer.jdbbackup;

public class Parameters {
	private String dbHost;
	private String dbPort;
	private String dbUser;
	private String dbPwd;
	private String mySQLPath;

	public Parameters() {
		this.dbHost = "127.0.0.1";
		this.dbPort = "3306";
		this.dbUser = "root";
		this.dbPwd = "";
		this.mySQLPath = "";
	}

	public String getMySQLPath() {
		return mySQLPath;
	}

	public void setMySQLPath(String mySQLPath) {
		this.mySQLPath = mySQLPath;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}
}
