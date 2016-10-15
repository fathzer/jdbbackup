package com.fathzer.jdbbackup.managers;

import java.io.File;
import java.io.IOException;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.InvalidArgument;
import com.fathzer.jdbbackup.PathDecoder;

/** A destination manager that saves the backups locally.
 * <br>It uses an instance of {@link DefaultPathDecoder} in order to build the destination path.
 * To change this behaviour, you should override the {@link #getPathDecoder()} method.
 */
public class FileManager implements DestinationManager {
	public FileManager() {
		super();
	}

	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		return new File(getPathDecoder().decodePath(fileName));
	}

	@Override
	public String send(File file) throws IOException {
		// Do nothing, file is already saved at the right place
		return "Saved to: "+file.getAbsolutePath();
	}

	@Override
	public PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}

	@Override
	public String getProtocol() {
		return "file";
	}
}
