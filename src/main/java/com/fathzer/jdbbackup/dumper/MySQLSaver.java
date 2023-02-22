package com.fathzer.jdbbackup.dumper;

import java.util.ArrayList;
import java.util.List;

import com.fathzer.jdbbackup.Options;

/** A DBSaver that saves MYSQL database.
 * <br>It requires mysqldump to be installed on the machine.
 */
public class MySQLSaver extends DBSaverFromProcess {
	@Override
	protected List<String> getCommand(Options params) {
		List<String> commands = new ArrayList<>();
		commands.add("mysqldump");
		commands.add("--host="+params.getDbHost());
		commands.add("--port="+params.getDbPort());
		commands.add("--user="+params.getDbUser());
		if (params.getDbPwd()!=null && !params.getDbPwd().isEmpty()) {
			commands.add("--password="+params.getDbPwd());
		}
		commands.add("--add-drop-database");
		commands.add(params.getDbName());
		return commands;
	}

	@Override
	public String getDBType() {
		return "mysql";
	}
}
