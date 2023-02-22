package com.fathzer.jdbbackup;

/** Interface of classes able to decode a path. 
 */
public interface PathDecoder {
	/** Decodes a path.
	 * @param path The encoded path
	 * @return The decoded path
	 * @throws IllegalNamePatternException if the path has wrong format
	 */
	String decodePath(String path) throws IllegalNamePatternException;
}
