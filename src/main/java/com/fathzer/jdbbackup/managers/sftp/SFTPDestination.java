package com.fathzer.jdbbackup.managers.sftp;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.InvalidArgumentException;

public class SFTPDestination {
	private String user;
	private String password;
	private String host;
	private int port;
	private String path;
	private String filename;

	/** Constructor.
	 * @param destination The destination in its string format: <i>user:pwd@host[:port][/path]/filename</i>
	 */
	public SFTPDestination(String destination) {
		int index = destination.indexOf('/');
		if (index < 0) {
			badFileName(destination);
		}
		parseConnectionData(destination, destination.substring(0, index));
		parsePath(destination, destination.substring(index + 1));
	}

	private void parseConnectionData(String fileName, String cData) throws InvalidArgumentException {
		int index = cData.indexOf('@');
		if (index < 0) {
			badFileName(fileName);
		}
		parseUserData(fileName, cData.substring(0, index));
		parseHostData(fileName, cData.substring(index + 1));
	}

	private void parseUserData(String fileName, String userData) throws InvalidArgumentException {
		int index = userData.indexOf(':');
		if (index < 0) {
			badFileName(fileName);
		}
		this.user = userData.substring(0, index);
		this.password = userData.substring(index + 1);
	}

	private void parseHostData(String fileName, String hostData) throws InvalidArgumentException {
		if (hostData.isEmpty()) {
			this.host = "127.0.0.1";
			this.port = 22;
		} else {
			int index = hostData.indexOf(':');
			if (index < 0) {
				this.port = 22;
				this.host = hostData;
			} else {
				this.host = hostData.substring(0, index);
				try {
					this.port = Integer.parseInt(hostData.substring(index + 1));
				} catch (NumberFormatException e) {
					badFileName(fileName);
				}
			}
		}
	}

	private void parsePath(String fileName, String path) throws InvalidArgumentException {
		path = DefaultPathDecoder.INSTANCE.decodePath(path);
		int index = path.lastIndexOf('/');
		if (index < 0) {
			this.filename = path;
		} else {
			this.path = path.substring(0, index);
			this.filename = path.substring(index + 1);
		}
		if (filename.isEmpty()) {
			badFileName(fileName);
		}
	}

	private void badFileName(String fileName) throws InvalidArgumentException {
		throw new InvalidArgumentException(
				fileName + " does not match format user:pwd@host[:port][/path]/filename");
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

	public String getPath() {
		return path;
	}

	public String getFilename() {
		return filename;
	}
}