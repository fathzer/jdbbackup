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

import org.junit.jupiter.api.Test;

import com.fathzer.jdbbackup.DBSaver;
import com.fathzer.jdbbackup.InvalidArgumentException;
import com.fathzer.jdbbackup.JDbBackup;

class DBSaverTest {
	static class CoolJDBackup extends JDbBackup {
		@Override
		public DBSaver getDBSaver(String dbType) throws InvalidArgumentException {
			return super.getDBSaver(dbType);
		}
	}
	

	@Test
	void test() throws IOException {
		CoolJDBackup b = new CoolJDBackup();
		DBSaver s = b.getDBSaver(new FakeJavaSaver().getDBType());
		File f = File.createTempFile("DBDump", ".gz");
		f.deleteOnExit();
		try {
			s.save(null, f);
		} catch (IOException e) {
			System.err.println("WARNING, can't launch java, "+DBSaver.class+" is skipped");
			e.printStackTrace();
		}
		assertNotEquals(0, f.length());
		try (BufferedReader reader=new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f))))) {
			final List<String> lines = reader.lines().collect(Collectors.toList());
			assertEquals(FakeJavaSaver.CONTENT, lines);
		}
	}

}
