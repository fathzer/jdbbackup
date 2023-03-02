package com.fathzer.jdbbackup.managers.dropbox;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.dropbox.core.oauth.DbxCredential;
import com.fathzer.jdbbackup.managers.dropbox.DropBoxManager.DropBoxDestination;
import com.fathzer.jdbbackup.utils.BasicExtensionBuilder;

class DropboxManagerTest {

	@Test
	void test() {
		DropBoxManager manager = new DropBoxManager();
		DropBoxDestination path = manager.setDestinationPath("token/a/{d=MMyy}", BasicExtensionBuilder.INSTANCE);
		assertEquals("token", path.getToken());
		assertEquals("/a/"+new SimpleDateFormat("MMyy").format(new Date())+".sql.gz", path.getPath());
		//TODO Not sure this is a good idea to have a leading /
	}

	@Test
	void testCredentials() {
		DropBoxManager manager = new DropBoxManager();
		{
			final DbxCredential credential = manager.getCredential("token");
			assertAll(
				() -> assertEquals("token",credential.getAccessToken()),
				() -> assertFalse(credential.aboutToExpire()),
				() -> assertNull(credential.getRefreshToken())
				);
		}
		{
			final DbxCredential credential = manager.getCredential(DropBoxBase.REFRESH_PREFIX+"token");
			assertAll(
				() -> assertTrue(credential.aboutToExpire()),
				() -> assertEquals("token",credential.getRefreshToken())
				);
		}
	}
}
