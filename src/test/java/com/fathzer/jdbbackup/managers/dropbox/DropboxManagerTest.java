package com.fathzer.jdbbackup.managers.dropbox;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.fathzer.jdbbackup.managers.dropbox.DropBoxManager.DropBoxDestination;
import com.fathzer.jdbbackup.utils.DefaultExtensionBuilder;

class DropboxManagerTest {

	@Test
	void test() {
		DropBoxDestination path = new DropBoxManager().setDestinationPath("token/a/{d=MMyy}", DefaultExtensionBuilder.INSTANCE);
		assertEquals("token", path.getToken());
		//TODO Not sure this is a good idea to have a leading /
		assertEquals("/a/"+new SimpleDateFormat("MMyy").format(new Date())+".sql.gz", path.getPath());
	}

}
