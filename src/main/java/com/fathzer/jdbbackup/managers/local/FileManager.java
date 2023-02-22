package com.fathzer.jdbbackup.managers.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.InvalidArgumentException;
import com.fathzer.jdbbackup.ProxyOptions;

/** A destination manager that saves the backups locally.
 * <br>It uses an instance of {@link DefaultPathDecoder} in order to build the destination path.
 * To change this behaviour, you should override the {@link #getPathDecoder()} method.
 */
public class FileManager implements DestinationManager<Path> {
	public FileManager() {
		super();
	}

	@Override
	public void setProxy(ProxyOptions options) {
		// Ignore proxy as there's no network access there.
	}

	@Override
	public Path setDestinationPath(String fileName) throws InvalidArgumentException {
		return new File(getPathDecoder().decodePath(fileName)).toPath();
	}

	@Override
	public String send(File tmpFile, Path dest) throws IOException {
	    Files.copy(tmpFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
		return "Saved to: "+dest.toFile().getAbsolutePath();
	}

	@Override
	public String getProtocol() {
		return "file";
	}
}
