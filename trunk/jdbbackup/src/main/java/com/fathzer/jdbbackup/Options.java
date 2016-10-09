package com.fathzer.jdbbackup;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Options extends ProxyOptions {
    @Option(name = "-h", metaVar="dbHost", usage = "Data base server host name")
    private String dbHost="127.0.0.1";

    @Option(name = "-p", metaVar="dbPort", usage = "Data base server port")
    private int dbPort=3306;

    @Argument(index = 0, metaVar="base", usage ="Data base name", required=true)
    private String dbName;
    
    @Option(name = "-u", metaVar="dbUser", usage = "Data base user")
    private String dbUser="root";

    @Option(name = "-pwd", metaVar="dbPassword", usage = "Data base user password")
    private String dbPwd;

    @Argument(index = 1, metaVar="dest", usage ="Destination", required=true)
    private String dest;

	public String getDbName() {
		return dbName;
	}

	public String getDestination() {
		return dest;
	}

	public String getDbHost() {
		return dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setDestination(String destination) {
		this.dest = destination;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public void setDbPort(int dbPort) {
		this.dbPort = dbPort;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}
}
