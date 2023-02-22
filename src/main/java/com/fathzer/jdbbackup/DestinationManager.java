package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** An abstract class to manage where backup are saved.
 */
public interface DestinationManager<T> {
	void setProxy(final ProxyOptions options);
	
	/** Gets the string that identifies the protocol.
	 * <br>Example file, dropbox, sftp. This destination manager will have to process all file transfers related to destinations that begins with <i>protocol</i>:// 
	 * @return a String
	 */
	String getProtocol();

	/** Gets the path decoder used to decode the path when calling {@link #setDestinationPath(String)}.
	 * @return a path decoder.
	 */
	default PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}

	/** Sets the destination of next backup. 
	 * @param path The path as it is entered in the command line (example:"{d=YYYY}/baseName")
	 * @return An internal representation of where the backup will be saved.
	 * @throws InvalidArgumentException If the path is not valid.
	 */
	T setDestinationPath(final String path) throws InvalidArgumentException;
	
	/** Sends the backup file to its final destination at the path passed in {@link #setDestinationPath}.
	 * <br>It is guaranteed that {@link DestinationManager#setProxy(ProxyOptions)} will be called before this method.
	 * @param file The temporary file to save
	 * @param destination The destination that was returned by {@link #getDestination(String)}
	 * @return a message indicating where the file was sent
	 * @throws IOException If an error occurs while sending the file
	 * @see #setProxy(ProxyOptions)
	 */
	String send(final File file, T destination) throws IOException;
}
