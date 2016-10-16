package com.fathzer.jdbbackup.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.InvalidArgument;
import com.fathzer.jdbbackup.PathDecoder;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPManager implements DestinationManager {
	private String user;
	private String password;
	private String host;
	private int port;
	private String destPath;
	private String destFilename;
	
	public SFTPManager() {
		super();
	}

	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		// fileName should have the following format: user:pwd@host[:port][/path]/filename
		int index = fileName.indexOf('/');
		if (index<0) {
			badFileName(fileName);
		}
		parseConnectionData(fileName, fileName.substring(0, index));
		parsePath(fileName, fileName.substring(index+1));
		return null;
	}

	private void parseConnectionData(String fileName, String cData) throws InvalidArgument {
		int index = cData.indexOf('@');
		if (index<0) {
			badFileName(fileName);
		}
		parseUserData(fileName, cData.substring(0, index));
		parseHostData(fileName, cData.substring(index+1));
	}

	private void parseUserData(String fileName, String userData) throws InvalidArgument {
		int index = userData.indexOf(':');
		if (index<0) {
			badFileName(fileName);
		}
		this.user = userData.substring(0, index);
		this.password = userData.substring(index+1);
	}

	private void parseHostData(String fileName, String hostData) throws InvalidArgument {
		if (hostData.isEmpty()) {
			this.host = "127.0.0.1";
			this.port = 22;
		} else {
			int index = hostData.indexOf(':');
			if (index<0) {
				this.port = 22;
				this.host = hostData;
			} else {
				this.host = hostData.substring(0, index);
				try {
					this.port = Integer.parseInt(hostData.substring(index+1));
				} catch (NumberFormatException e) {
					badFileName(fileName);
				}
			}
		}
	}

	private void parsePath(String fileName, String path) throws InvalidArgument {
		path = getPathDecoder().decodePath(path);
		int index = path.lastIndexOf('/');
		if (index<0) {
			this.destFilename = path;
		} else {
			this.destPath = path.substring(0, index);
			this.destFilename = path.substring(index+1);
		}
		if (destFilename.isEmpty()) {
			badFileName(fileName);
		}
	}

	private void badFileName(String fileName) throws InvalidArgument {
		throw new InvalidArgument(fileName+" does not match format user:pwd@host[:port][/path]/filename");
	}

	@Override
	public String send(File file) throws IOException {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			try {
				ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
				try {
					channel.connect();
					if (destPath!=null) {
						mkdirs(channel, destPath);
						channel.cd(destPath);
					}
					channel.put(new FileInputStream(file), destFilename);
					String fullPath = destPath==null ? destFilename : destPath+"/"+destFilename;
					return "Sent to "+user+"@"+host+": "+fullPath;
				} catch (SftpException e) {
					throw new IOException(e);
				} finally {
					channel.exit();
					channel.disconnect();
				}
			} finally {
				session.disconnect();
			}
		} catch (JSchException e) {
			throw new IOException(e);
		}
	}
	
	public static void mkdirs(ChannelSftp ch, String path) throws SftpException {
		List<String> folders = new ArrayList<>(Arrays.asList(path.split("/")));
		String fullPath;
		if (folders.get(0).isEmpty()) {
			fullPath = "/";
			folders.remove(0);
		} else {
			fullPath = "./";
		}
		for (String folder : folders) {
			Vector<?> ls = ch.ls(fullPath);
			boolean isExist = false;
			for (Object o : ls) {
				if (o instanceof LsEntry) {
					LsEntry e = (LsEntry) o;
					if (e.getAttrs().isDir() && e.getFilename().equals(folder)) {
						isExist = true;
					}
				}
			}
			if (!isExist && !folder.isEmpty()) {
				ch.mkdir(fullPath + folder);
			}
			fullPath = fullPath + folder + "/";
		}
	}

	@Override
	public PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}

	@Override
	public String getProtocol() {
		return "sftp";
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getDestPath() {
		return destPath;
	}

	public String getDestFilename() {
		return destFilename;
	}
}
