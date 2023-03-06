package com.fathzer.jdbbackup.managers.dropbox;

import static org.junit.jupiter.api.Assertions.*;

import java.util.MissingResourceException;

import org.junit.jupiter.api.Test;

class DropBoxBaseTest {

	@Test
	void test() {
		DropBoxBase base = new DropBoxBase();
//		DbxRequestConfig config = base.getConfig();
		base.setDbxAppInfoSupplier(() -> DropBoxBase.RESOURCE_PROPERTY_APP_INFO_BUILDER.apply("wrongAppFile1.properties"));
		assertThrows(MissingResourceException.class, () -> base.getAppInfo());
		base.setDbxAppInfoSupplier(() -> DropBoxBase.RESOURCE_PROPERTY_APP_INFO_BUILDER.apply("wrongAppFile2.properties"));
		assertThrows(MissingResourceException.class, () -> base.getAppInfo());
	}

}
