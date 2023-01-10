package com.fathzer.jdbbackup;

import java.util.ArrayList;
import java.util.List;

/** A DBSaver that saves MYSQL database.
 * <br>It requires mysqldump to be installed on the machine.
 */
public class MySQLSaver extends DBSaver {
	@Override
	protected List<String> getCommand(Options params, String database) {
		List<String> commands = new ArrayList<>();
		commands.add("mysqldump");
		commands.add("--host="+params.getDbHost());
		commands.add("--port="+params.getDbPort());
		commands.add("--user="+params.getDbUser());
		if (params.getDbPwd()!=null && !params.getDbPwd().isEmpty()) {
			commands.add("--password="+params.getDbPwd());
		}
		commands.add("--add-drop-database");
		commands.add(database);
		return commands;
	}
}
