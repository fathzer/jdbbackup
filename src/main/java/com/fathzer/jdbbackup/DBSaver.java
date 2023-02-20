package com.fathzer.jdbbackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A class able to save a database to a compressed (.gz) file.
 * <br>Data base dump is obtained through a command line.
 */
public abstract class DBSaver {
	protected DBSaver() {
		super();
	}
	
	/** Saves a database to the specified location.
	 * @param params The parameters to access to database to save 
	 * @param destFile the backup destination file or null to backup database in a temporary file.
	 * @return The file where database has been saved.
	 * @throws IOException If something went wrong
	 */
	public File save(Options params, File destFile) throws IOException {
		final List<String> commands = getCommand(params, params.getDbName());

		final ProcessBuilder pb = new ProcessBuilder(commands);
		if (destFile==null) {
			destFile = File.createTempFile("DBDump", ".gz");
			destFile.deleteOnExit();
		} else {
			File parentFile = destFile.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Unable to create directory "+parentFile);
			}
		}
		final Process process = pb.start();
		final ProcessContext context = new ProcessContext(process);
		final Compressor compressor = new Compressor(destFile, context);
		final Thread compressThread = new Thread(compressor);
		compressThread.start();
		final InputStream err = process.getErrorStream();
		Thread errorThread = new Thread(() -> {
				final Logger logger = LoggerFactory.getLogger(DBSaver.this.getClass());
				try (BufferedReader bufErr = new BufferedReader(new InputStreamReader(err))) {
					for (String line = bufErr.readLine(); line!=null; line = bufErr.readLine()) {
						logger.warn(line);
					}
				} catch (IOException e) {
					if (!context.isKilled()) {
						context.kill();
						logger.error("Error while reading error stream", e);
					}
				}
			});
		errorThread.start();
		
		try {
			final int result = process.waitFor();
			compressThread.join();
			errorThread.join();
			if (compressor.getError()!=null) {
				throw compressor.getError();
			}
			if (result!=0) {
				throw new RuntimeException ("Process failed"); //TODO
			}
			return destFile;
		} catch (InterruptedException e) {
			LoggerFactory.getLogger(getClass()).error("Backup was interrupted", e);
			Thread.currentThread().interrupt();
			return null;
		}
    }

	/** Gets the command line to execute to save the database.
	 * @param params The database access parameters
	 * @param database The name of the database
	 * @return The list of the tokens that compose the command 
	 */
	protected abstract List<String> getCommand(Options params, String database);
}