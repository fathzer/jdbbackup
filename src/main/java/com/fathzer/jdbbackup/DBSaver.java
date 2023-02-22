package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** A class able to save a database to a compressed file.
 */
public interface DBSaver {
	/** Saves a database to the specified location.
	 * @param params The parameters to access to database to save 
	 * @param destFile the backup destination file or null to backup database in a temporary file.
	 * @return The file where database has been saved or null if the backup was interrupted.
	 * @throws IOException If something went wrong
	 */
	void save(Options params, File destFile) throws IOException;
}
