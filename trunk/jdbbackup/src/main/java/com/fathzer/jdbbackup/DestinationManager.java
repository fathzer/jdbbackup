package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** An abstract class to manage where backup are saved.
 */
public abstract class DestinationManager {
	private PathDecoder pathDecoder;
	/** Constructor.
	 * @param pathDecoder The instance used to decode the path.
	 */
	protected DestinationManager(PathDecoder pathDecoder) {
		this.pathDecoder = pathDecoder;
	}
	
	/** Gets the path decoder.
	 * @return a path decoder.
	 */
	public PathDecoder getPathDecoder() {
		return pathDecoder;
	}

	/** Sets the destination of next backup. 
	 * @param path The path as it is entered in the command line (example:"{d=YYYY}/baseName")
	 * @return The file where the backup should be saved in the local file system, null if this manager as no preferences on where this file is located.
	 * <br>If the final destination of the backup is the file system, this method will typically return the final destination.
	 * @throws InvalidArgument If the path is not valid.
	 */
	public abstract File setDestinationPath(String path) throws InvalidArgument;
	
	/** Sends the backup file to its final destination at the path passed in {@link #setDestinationPath}.
	 * @param file The temporary file
	 * @throws IOException If an error occurs while sending the file
	 */
	public abstract void send(File file) throws IOException;
}
