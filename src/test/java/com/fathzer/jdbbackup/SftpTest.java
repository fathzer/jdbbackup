package com.fathzer.jdbbackup;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fathzer.jdbbackup.managers.SFTPManager;

public class SftpTest {

	@Test
	public void test() throws InvalidArgument {
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
	public void testDefault() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("user:pwd@host/filename");
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(22, m.getPort());
		assertNull(m.getDestPath());
		assertEquals("filename.sql.gz", m.getDestFilename());
	}

	@Test (expected=InvalidArgument.class)
	public void testNoPwd() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("user@host/filename");
	}

	@Test (expected=InvalidArgument.class)
	public void testNoUser() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("host/filename");
	}

	@Test (expected=InvalidArgument.class)
	public void testNoHost() throws InvalidArgument {
		SFTPManager m = new SFTPManager();
		m.setDestinationPath("user:pwd/filename");
	}
}
