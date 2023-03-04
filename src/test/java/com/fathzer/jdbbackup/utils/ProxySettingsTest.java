package com.fathzer.jdbbackup.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProxySettingsTest {

	@Test
	void test() {
		ProxySettings settings = ProxySettings.fromString("user:pwd@host:3128");
		assertEquals("user", settings.getLogin().getUser());
		assertEquals("pwd", settings.getLogin().getPassword());
		assertEquals("host", settings.getHost());
		assertEquals(3128, settings.getPort());
		
		settings = new ProxySettings("host", 3128, new Login("user", "pwd")); 
		assertEquals("user", settings.getLogin().getUser());
		assertEquals("pwd", settings.getLogin().getPassword());
		assertEquals("host", settings.getHost());
		assertEquals(3128, settings.getPort());
		
		// Test toString hides the password
		ProxySettings fromToString = ProxySettings.fromString(settings.toString());
		assertEquals(settings.getLogin().getUser(), fromToString.getLogin().getUser());
		assertEquals(settings.getHost(), fromToString.getHost());
		assertEquals(settings.getPort(), fromToString.getPort());
		assertNotEquals(settings.getLogin().getPassword(), fromToString.getLogin().getPassword());

		// Test with no password
		settings = ProxySettings.fromString("user@host:2000");
		assertEquals("user", settings.getLogin().getUser());
		assertNull(settings.getLogin().getPassword());
		assertEquals("host", settings.getHost());
		assertEquals(2000, settings.getPort());
		assertEquals("user@host:2000", settings.toString());
		
		// Test with no user
		settings = ProxySettings.fromString("host:3128");
		assertEquals("host", settings.getHost());
		assertEquals(3128, settings.getPort());
		assertEquals("host:3128", settings.toString());
		
		// Empty or null String
		assertNull(ProxySettings.fromString(" "));
		assertNull(ProxySettings.fromString(null));
		
		// Illegal arguments
		assertThrows(IllegalArgumentException.class, () -> ProxySettings.fromString("host"));
		assertThrows(IllegalArgumentException.class, () -> ProxySettings.fromString("host:3128:11"));
		assertThrows(IllegalArgumentException.class, () -> ProxySettings.fromString("host:xxx"));
	}

}
