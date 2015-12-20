package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** A destination manager that saves the backups locally.
 * <br>It uses an instance of {@link DefaultPathDecoder} in order to build the destination path.
 * To change this behaviour, you should override the {@link #getPathDecoder()} method.
 */
public class FileManager extends DestinationManager {
	protected FileManager() {
		super();
	}

	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		return new File(getPathDecoder().decodePath(fileName));
	}

	@Override
	public void send(File file) throws IOException {
		// Do nothing, file is already saved at the right place
	}

	@Override
	public PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}
}
