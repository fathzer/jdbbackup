package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

/** A file manager that saves the backups locally.
 */
public class FileManager extends DestinationManager {
	protected FileManager(PathDecoder pathDecoder) {
		super(pathDecoder);
	}

	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		return new File(getPathDecoder().decodePath(fileName));
	}

	@Override
	public void send(File file) throws IOException {
		// Do nothing, file is already saved at the right place
	}

}
