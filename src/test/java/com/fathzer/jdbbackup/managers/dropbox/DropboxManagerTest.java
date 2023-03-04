package com.fathzer.jdbbackup.managers.dropbox;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.dropbox.core.oauth.DbxCredential;
import com.fathzer.jdbbackup.managers.dropbox.DropBoxManager.DropBoxDestination;
import com.fathzer.jdbbackup.utils.BasicExtensionBuilder;

class DropboxManagerTest {

	@Test
	void test() {
		DropBoxManager manager = new DropBoxManager();
		try (MockedConstruction<Date> mock = mockConstruction(Date.class)) {
			DropBoxDestination path = manager.validate("token/a/{d=MMyy}", BasicExtensionBuilder.INSTANCE);
			assertEquals("token", path.getToken());
			assertEquals("/a/0170.sql.gz", path.getPath());
		}
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
