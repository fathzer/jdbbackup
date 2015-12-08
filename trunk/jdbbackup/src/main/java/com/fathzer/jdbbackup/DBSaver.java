package com.fathzer.jdbbackup;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBSaver {
	public DBSaver() {
		super();
	}
	
	public File save(DBParameters params, String database, File destFile) throws IOException {
		List<String> commands = getCommand(params, database);

		ProcessBuilder pb = new ProcessBuilder(commands);
		if (destFile==null) {
			destFile = File.createTempFile("DBDump", ".gzip");
			destFile.deleteOnExit();
		}
		Process process = pb.start();
		final InputStream in = process.getInputStream();
		Compressor compressor = new Compressor(destFile, in);
		Thread compressThread = new Thread(compressor);
		compressThread.start();
		final InputStream err = process.getErrorStream();
		Thread errorThread = new Thread(new Runnable(){
			@Override
			public void run() {
				Logger logger = LoggerFactory.getLogger(DBSaver.this.getClass());
				try (BufferedReader bufErr = new BufferedReader(new InputStreamReader(err))) {
					for (String line = bufErr.readLine(); line!=null; line = bufErr.readLine()) {
						logger.warn(line);
					}
				} catch (IOException e) {
					logger.error("Error while reading error stream", e);
				}
			}});
		errorThread.start();
		
		try {
			int result = process.waitFor();
			compressThread.join();
			errorThread.join();
			if (compressor.getError()!=null) {
				throw compressor.getError();
			}
			return result == 0 && compressor.getError()==null ? destFile : null;
		} catch (InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Backup was interrupted", e);
			return null;
		}
    }

	protected List<String> getCommand(DBParameters params, String database) {
		List<String> commands = new ArrayList<String>();
		commands.add("mysqldump");
		commands.add("--host="+params.getHost());
		commands.add("--port="+params.getPort());
		commands.add("--user="+params.getUser());
		if (!params.getPwd().isEmpty()) {
			commands.add("--password="+params.getPwd());
		}
		commands.add("--add-drop-database");
		commands.add(database);
		return commands;
	}
}