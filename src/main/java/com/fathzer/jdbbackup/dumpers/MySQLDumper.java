package com.fathzer.jdbbackup.dumpers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fathzer.jdbbackup.utils.Login;

/** A DBSaver that saves MYSQL database.
 * <br>It requires mysqldump to be installed on the machine.
 * //TODO URI format documentation 
 */
public class MySQLDumper extends DBDumperFromProcess {
	@Override
	protected List<String> getCommand(URI params) {
		final Login login = Login.fromString(params.getUserInfo());  
		if (isEmpty(params.getPath()) || isEmpty(params.getHost()) || params.getPort()<=0 || login==null) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		final List<String> commands = new ArrayList<>();
		commands.add("mysqldump");
		commands.add("--host="+params.getHost());
		commands.add("--port="+params.getPort());
		commands.add("--user="+login.getUser());
		if (!isEmpty(login.getPassword())) {
			commands.add("--password="+login.getPassword());
		}
		commands.add("--add-drop-database");
		commands.add(params.getPath());
		return commands;
	}

	@Override
	public String getScheme() {
		return "mysql";
	}
	
	private boolean isEmpty(String str) {
		return str==null || str.trim().isEmpty();
	}
}
