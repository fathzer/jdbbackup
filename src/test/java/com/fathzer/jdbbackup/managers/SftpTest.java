package com.fathzer.jdbbackup.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.jdbbackup.managers.sftp.SFTPDestination;

class SftpTest {

	@Test
	void test() {
		SFTPDestination m = new SFTPDestination("user:pwd@host:2222/path1/path2/filename");
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(2222, m.getPort());
		assertEquals("path1/path2", m.getPath());
		assertEquals("filename.sql.gz", m.getFilename());
	}

	@Test
	void testDefault() {
		SFTPDestination m = new SFTPDestination("user:pwd@host/filename");
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(22, m.getPort());
		assertNull(m.getPath());
		assertEquals("filename.sql.gz", m.getFilename());
	}

	@Test
	void testWrongDestPath() {
		// Missing pwd
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user@host/filename"));
		// Missing login
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("host/filename"));
		// Missing host
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user:pwd/filename"));
	}
}
