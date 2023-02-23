package com.fathzer.jdbbackup.dumper;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import com.fathzer.jdbbackup.DBSaver;
import com.fathzer.jdbbackup.JDbBackup;

class DBSaverTest {

	static class CoolJDBackup extends JDbBackup {
		@Override
		public DBSaver getDBSaver(String dbType) {
			return super.getDBSaver(dbType);
		}
	}

	@Test
//	@EnabledIf("com.fathzer.jdbbackup.JavaProcessAvailabilityChecker#available")
	@Disabled("Replace by JDbBackupTest")
	void test() throws IOException {
		CoolJDBackup b = new CoolJDBackup();
		FakeJavaSaver s = (FakeJavaSaver) b.getDBSaver(new FakeJavaSaver().getDBType());
		s.shouldFail = false;
		File f = File.createTempFile("DBDump", ".gz");
		f.deleteOnExit();
		s.save(null, f);
		assertNotEquals(0, f.length());
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(new FileInputStream(f))))) {
			final List<String> lines = reader.lines().collect(Collectors.toList());
			assertEquals(FakeJavaSaver.CONTENT, lines);
		}
	}

}
