package com.fathzer.jdbbackup.managers.sftp;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

class SFTPManagerTest {

	@Test
	void moreTest() throws JSchException, IOException {
		SFTPManager manager = new SFTPManager() {
			@Override
			String send(Session session, SFTPDestination dest, File file) throws JSchException, IOException {
				return "done";
			}
		};
		
		final Session session = mock(Session.class);
		try (MockedConstruction<JSch> mock = mockConstruction(JSch.class, (jsch , context) -> {
			when(jsch.getSession("user","127.0.0.1",22)).thenReturn(session);
		})) {
			SFTPDestination dest = new SFTPDestination("user:pwd/path/filename",x->x);
			
			manager.send(new File("toto"), dest);
			verify(session).setPassword("pwd");
		}
	}
}
