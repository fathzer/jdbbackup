package com.fathzer.jdbbackup.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DefaultExtensionBuilderTest {

	@Test
	void test() {
		DefaultExtensionBuilder eb = DefaultExtensionBuilder.INSTANCE;
		assertTrue(eb.hasExtension("x/b.zip"));
		assertFalse(eb.hasExtension("x.zip/b"));
		assertEquals("a.sql.gz", eb.apply("a"));
		assertEquals("a.zip", eb.apply("a.zip"));
	}

}
