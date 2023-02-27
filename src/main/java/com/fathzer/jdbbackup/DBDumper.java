package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** A class able to dump a database to a file.
 */
public interface DBDumper {
	/** Gets the type of database this saver can dump.
	 * @return a string (example mysql)
	 */
	String getDBType();
	
	
	/** Saves a database to the specified location.
	 * @param params The parameters to access to database to save 
	 * @param destFile the backup destination file or null to backup database in a temporary file.
	 * @throws IOException If something went wrong
	 */
	void save(Options params, File destFile) throws IOException;
}
