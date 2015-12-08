package com.fathzer.jdbbackup;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Options {
    public enum Target { FILE, DROPBOX };

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

    @Option(name="-t", metaVar="target", usage = "Where to store the backup")
    private Target target = Target.FILE;
    
    @Option(name="-f", metaVar="dateFormat", usage = "A date format that will be add to dest file name")
    private String format;

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

	public Target getTarget() {
		return target;
	}

	public String getFormat() {
		return format;
	}
}
