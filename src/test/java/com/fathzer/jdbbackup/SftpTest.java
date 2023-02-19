package com.fathzer.jdbbackup;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.jdbbackup.managers.SFTPManager;

class SftpTest {

	@Test
	void test() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("user:pwd@host:2222/path1/path2/filename");
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(2222, m.getPort());
		assertEquals("path1/path2", m.getDestPath());
		assertEquals("filename.sql.gz", m.getDestFilename());
	}

	@Test
	void testDefault() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("user:pwd@host/filename");
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(22, m.getPort());
		assertNull(m.getDestPath());
		assertEquals("filename.sql.gz", m.getDestFilename());
	}

	@Test
	void testWrongDestPath() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		// Missing pwd
		assertThrows(InvalidArgument.class, () -> m.setDestinationPath("user@host/filename"));
		// Missing login
		assertThrows(InvalidArgument.class, () -> m.setDestinationPath("host/filename"));
		// Missing host
		assertThrows(InvalidArgument.class, () -> m.setDestinationPath("user:pwd/filename"));
	}
}
