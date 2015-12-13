package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

public interface FileManager {
	File setFileName(String fileName) throws InvalidArgument;
	void send(File destFile) throws IOException;
}
