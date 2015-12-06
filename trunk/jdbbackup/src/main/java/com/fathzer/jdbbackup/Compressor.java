package com.fathzer.jdbbackup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

final class Compressor implements Runnable {
	private final File destFile;
	private final InputStream in;
	private volatile IOException err;

	Compressor(File destFile, InputStream in) {
		this.destFile = destFile;
		this.in = in;
	}

	@Override
	public void run() {
		try (OutputStream out = new GZIPOutputStream(new FileOutputStream(destFile))) {
			for (int c=in.read(); c!=-1; c=in.read()) {
				out.write(c);
			}
			out.close();
		} catch (IOException e) {
			this.err = e;
		}
	}

	public IOException getError() {
		return err;
	}
}