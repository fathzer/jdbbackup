package com.fathzer.jdbbackup.dumpers;

import java.util.ArrayList;
import java.util.List;

import com.fathzer.jdbbackup.Options;

/** A DBSaver that saves MYSQL database.
 * <br>It requires mysqldump to be installed on the machine.
 */
public class MySQLDumper extends DBDumperFromProcess {
	@Override
	protected List<String> getCommand(Options params) {
		if (isEmpty(params.getDbName()) || isEmpty(params.getDbHost()) || params.getDbPort()<=0 || isEmpty(params.getDbUser())) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		final List<String> commands = new ArrayList<>();
		commands.add("mysqldump");
		commands.add("--host="+params.getDbHost());
		commands.add("--port="+params.getDbPort());
		commands.add("--user="+params.getDbUser());
		if (!isEmpty(params.getDbPwd())) {
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
	
	private boolean isEmpty(String str) {
		return str==null || str.trim().isEmpty();
	}
}
