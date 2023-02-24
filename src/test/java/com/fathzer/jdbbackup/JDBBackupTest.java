package com.fathzer.jdbbackup;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import com.fathzer.jdbbackup.dumper.FakeJavaSaver;

class JDBBackupTest {
	private static final String DEST_PATH = "./tmpTestFile.gz";
	
	private static class ObservableJDbBackup extends JDbBackup {
		private File tmpFile;

		@Override
		protected File createTempFile() throws IOException {
			this.tmpFile = super.createTempFile();
			return tmpFile;
		}
	}
	
	@AfterEach
	void cleanup() {
		new File(DEST_PATH).delete();
	}

	@Test
	@EnabledIf("com.fathzer.jdbbackup.JavaProcessAvailabilityChecker#available")
	void testOk() throws IOException {
		final ObservableJDbBackup b = new ObservableJDbBackup();
		final Options o = new Options();
		// No destination
		assertThrows(IllegalArgumentException.class, () -> b.backup(o));
		o.setDestination("file://"+DEST_PATH);

		// No DbName
		assertTrue(b.tmpFile==null || !b.tmpFile.exists());
		assertThrows(IllegalArgumentException.class, () -> b.backup(o));
		assertTrue(b.tmpFile==null || !b.tmpFile.exists());
		
		FakeJavaSaver.shouldFail = false;
		o.setDbType(new FakeJavaSaver().getDBType());
		b.backup(o);
		assertTrue(b.tmpFile==null || !b.tmpFile.exists());
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(DEST_PATH)))))) {
			final List<String> lines = reader.lines().collect(Collectors.toList());
			assertEquals(FakeJavaSaver.CONTENT, lines);
		}
	}

	@Test
	@EnabledIf("com.fathzer.jdbbackup.JavaProcessAvailabilityChecker#available")
	void testKo() throws IOException {
		final ObservableJDbBackup b = new ObservableJDbBackup();
		final Options o = new Options();
		o.setDestination("file://"+DEST_PATH);
		FakeJavaSaver.shouldFail = true;
		o.setDbType(new FakeJavaSaver().getDBType());
		assertThrows(IOException.class, () -> b.backup(o));
	}

	@Test
	@EnabledIf("com.fathzer.jdbbackup.JavaProcessAvailabilityChecker#available")
	void testDropbox() throws IOException {
		final ObservableJDbBackup b = new ObservableJDbBackup();
		final Options o = new Options();
		o.setDestination("dropbox://"+"refresh-FvDoHXhqlHsAAAAAAAAAASGS2_hcq8Jxf1wnkYoaLwtqRkkD0JtAZfyOVmxfbtdX/testJM");
		FakeJavaSaver.shouldFail = false;
		o.setDbType(new FakeJavaSaver().getDBType());
		b.backup(o);
	}
}
