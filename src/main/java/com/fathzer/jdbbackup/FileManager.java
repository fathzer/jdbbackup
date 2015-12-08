package com.fathzer.jdbbackup;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;

public interface FileManager {
	void parseFileName(String fileName) throws CmdLineException;
	void send() throws IOException;
}
