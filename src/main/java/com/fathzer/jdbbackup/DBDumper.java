package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/** A class able to dump a database to a file.
 */
public interface DBDumper {
	/** Gets the scheme used in the URL to identify the type of database this saver can dump.
	 * @return a string (example mysql)
	 */
	String getScheme();
	
	
	/** Saves a database to the specified location.
	 * @param srcURI The uri of the database to save (Its format depends on the DBDumper)
	 * @param destFile the backup destination file or null to backup database in a temporary file.
	 * @throws IOException If something went wrong
	 */
	void save(URI srcURI, File destFile) throws IOException;
}
