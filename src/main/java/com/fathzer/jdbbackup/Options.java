package com.fathzer.jdbbackup;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Options {
    @Option(name = "-h", metaVar="dbHost", usage = "Data base server host name")
    private String dbHost="127.0.0.1";

    @Option(name = "-p", metaVar="dbPort", usage = "Data base server port")
    private int dbPort=3306;

    @Option(name = "-u", metaVar="dbUser", usage = "Data base user")
    private String dbUser="root";

    @Option(name = "-pwd", metaVar="dbPassword", usage = "Data base user password")
    private String dbPwd="";

    @Argument(index = 0, metaVar="base", usage ="Data base name", required=true)
    private String dbName;
}
