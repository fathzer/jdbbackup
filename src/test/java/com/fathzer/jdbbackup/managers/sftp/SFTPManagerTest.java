package com.fathzer.jdbbackup.managers.sftp;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

class SFTPManagerTest {
	@Test
	void basicTest() throws JSchException, IOException, SftpException {
		SFTPManager manager = new SFTPManager();
		
		final Session session = mock(Session.class);
		final ChannelSftp channel = mock(ChannelSftp.class);
		when(session.openChannel("sftp")).thenReturn(channel);
		when(channel.ls("./")).thenReturn(new Vector<>());
		try (MockedConstruction<JSch> mock = mockConstruction(JSch.class, (jsch , context) -> {
			when(jsch.getSession("user","127.0.0.1",22)).thenReturn(session);
		})) {
			SFTPDestination dest = new SFTPDestination("user:pwd/path/filename",x->x);
			
			manager.send(null, 0, dest);
			verify(session).setPassword("pwd");
			verify(session, never()).setProxy(null);
			verify(session).connect();
			verify(session).disconnect();
			
			verify(channel).connect();
			verify(channel).ls("./");
			verify(channel).disconnect();
			verify(channel).mkdir("./path");
		}
	}
}
