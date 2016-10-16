package com.fathzer.jdbbackup;

import static org.junit.Assert.*;

import org.junit.Test;

public class DestinationTest {

	@Test
	public void test() {
		String path = "gkfsdjg-Sp_c/{d=yy}/photos-{d=dd-MM}";
		String type = "dropbox";
		Destination dest = new Destination(type+"://"+path);
		assertEquals(type,dest.getType());
		assertEquals(path,dest.getPath());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEmpty() {
		new Destination("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoType() {
		new Destination("gdfsjmlk");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoPath() {
		new Destination("xxx://");
	}
}
