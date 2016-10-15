package com.fathzer.jdbbackup.managers;

import java.io.File;
import java.io.IOException;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.InvalidArgument;
import com.fathzer.jdbbackup.PathDecoder;

public class SFTPManager implements DestinationManager {
	
	public SFTPManager() {
		super();
	}

	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		// TODO Auto-generated method stub
		System.out.println(fileName);
		return null;
	}

	@Override
	public String send(File file) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException("not yet done");
	}

	@Override
	public PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}

	@Override
	public String getProtocol() {
		return "sftp";
	}
}
