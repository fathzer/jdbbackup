package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** An abstract class to manage where backup are saved.
 */
public interface DestinationManager {
	
	/** Gets the path decoder used to decode the path when calling {@link #setDestinationPath(String)}.
	 * @return a path decoder.
	 */
	PathDecoder getPathDecoder();

	/** Sets the destination of next backup. 
	 * @param path The path as it is entered in the command line (example:"{d=YYYY}/baseName")
	 * @return The file where the backup should be saved in the local file system, null if this manager as no preferences on where this file is located.
	 * <br>If the final destination of the backup is the file system, this method will typically return the final destination.
	 * @throws InvalidArgument If the path is not valid.
	 */
	File setDestinationPath(String path) throws InvalidArgument;
	
	/** Sends the backup file to its final destination at the path passed in {@link #setDestinationPath}.
	 * @param file The temporary file
	 * @return a message indicating where the file was sent
	 * @throws IOException If an error occurs while sending the file
	 */
	String send(File file) throws IOException;
	
	String getProtocol();
}
