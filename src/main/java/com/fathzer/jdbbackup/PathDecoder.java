package com.fathzer.jdbbackup;

/** Interface of classes able to decode a path. 
 */
public interface PathDecoder {
	/** Decodes a path.
	 * @param path The encoded path
	 * @return The decoded path
	 * @throws IllegalNamePattern if the path has wrong format
	 */
	String decodePath(String path) throws IllegalNamePattern;
}
