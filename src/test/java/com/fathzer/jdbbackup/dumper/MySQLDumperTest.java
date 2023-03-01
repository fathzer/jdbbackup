package com.fathzer.jdbbackup.dumper;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fathzer.jdbbackup.dumpers.MySQLDumper;

class MySQLDumperTest {
	private static class MySQLObservableDumper extends MySQLDumper {
		@Override
		public List<String> getCommand(URI params) {
			return super.getCommand(params);
		}
	}
	
	@Test
	void test() {
		MySQLObservableDumper d = new MySQLObservableDumper();
		assertEquals("mysql",d.getScheme());
		List<String> command = d.getCommand(URI.create("mysql://u:p@host:4502/db/more"));
		expect(command, "u","p","host",4502,"db/more");
		command = d.getCommand(URI.create("mysql://user:pwd@localhost/db"));
		expect(command, "user","pwd","localhost",3306,"db");
		
		final URI wrongPort = URI.create("mysql://u:p@host:-5/db/more");
		assertThrows(IllegalArgumentException.class, () -> d.getCommand(wrongPort));
		final URI noLogin = URI.create("mysql://host:-5/db/more");
		assertThrows(IllegalArgumentException.class, () -> d.getCommand(noLogin));
		final URI noDb = URI.create("mysql://u:p@host");
		assertThrows(IllegalArgumentException.class, () -> d.getCommand(noDb));
	}
	
	private void expect(List<String> command, String user, String pwd, String host, int port, String db) {
		assertAll(command.toString(),
				() -> assertEquals("mysqldump", command.get(0)),
				() -> assertTrue(command.contains("--user="+user)),
				() -> assertTrue(command.contains("--password="+pwd)),
				() -> assertTrue(command.contains("--host="+host)),
				() -> assertTrue(command.contains("--port="+port)),
				() -> assertTrue(command.contains(db))
		);
	}
}
