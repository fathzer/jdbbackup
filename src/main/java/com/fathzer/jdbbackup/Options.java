package com.fathzer.jdbbackup;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Options extends ProxyOptions {
    @Argument(index = 0, metaVar="base", usage ="Data base name", required=true)
    private String dbName;
    
    @Argument(index = 1, metaVar="file", usage ="Destination file", required=true)
    private String fileName;

    @Option(name = "-h", metaVar="dbHost", usage = "Data base server host name")
    private String dbHost="127.0.0.1";

    @Option(name = "-p", metaVar="dbPort", usage = "Data base server port")
    private int dbPort=3306;

    @Option(name = "-u", metaVar="dbUser", usage = "Data base user")
    private String dbUser="root";

    @Option(name = "-pwd", metaVar="dbPassword", usage = "Data base user password")
    private String dbPwd;

    @Option(name="-t", metaVar="target", usage = "Where to store the backup (local file system, dropbox)")
    private String target = "file";
    
	public String getDbName() {
		return dbName;
	}

	public String getFileName() {
		return fileName;
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

	public String getTarget() {
		return target;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public void setTarget(String target) {
		this.target = target;
	}
}
