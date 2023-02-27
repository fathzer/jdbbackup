package com.fathzer.jdbbackup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DestinationTest {

	@Test
	void test() {
		String path = "gkfsdjg-Sp_c/{d=yy}/photos-{d=dd-MM}";
		String type = "dropbox";
		Destination dest = new Destination(type+"://"+path);
		assertEquals(type,dest.getProtocol());
		assertEquals(path,dest.getPath());

		assertThrows(IllegalArgumentException.class, () -> new Destination(""));
		assertThrows(IllegalArgumentException.class, () -> new Destination("gdfsjmlk"));
		assertThrows(IllegalArgumentException.class, () -> new Destination("xxx://"));
	}
}
