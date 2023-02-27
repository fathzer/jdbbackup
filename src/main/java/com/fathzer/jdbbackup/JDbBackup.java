package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.LoggerFactory;

/** A class able to perform a database backup.
 */
public class JDbBackup {
	private static final Map<String, DestinationManager<?>> MANAGERS = new HashMap<>();
	private static final Map<String, DBDumper> SAVERS = new HashMap<>();
	
	static {
		loadPlugins(ClassLoader.getSystemClassLoader());
	}
	
	/** Loads extra plugins.
	 * <br>Plugins allow you to extends this library to dump sources to destinations not supported by this library.
	 * <br>They are loaded using the {@link java.util.ServiceLoader} mechanism.
	 * @param classLoader The class loader used to load the plugins. For instance a class loader over jar files in a directory is exposed in <a href="https://stackoverflow.com/questions/16102010/dynamically-loading-plugin-jars-using-serviceloader">The second option exposed in this question</a>).
	 * @see DBDumper
	 * @see DestinationManager
	 */
	public static void loadPlugins(ClassLoader classLoader) {
		ServiceLoader.load(DestinationManager.class, classLoader).forEach(m -> MANAGERS.put(m.getProtocol(), m));
		ServiceLoader.load(DBDumper.class, classLoader).forEach(s -> SAVERS.put(s.getDBType(), s));
	}
	
	public JDbBackup() {
		super();
	}
	
	public String backup(Options options) throws IOException {
		final Destination destination = new Destination(options.getDestination());
		final DestinationManager<?> manager = getDestinationManager(destination);
		final File tmpFile = createTempFile();
		try {
			return backup(options, manager, destination, tmpFile);
		} finally {
			Files.delete(tmpFile.toPath());
		}
	}
	
	protected File createTempFile() throws IOException {
		final File tmpFile = Files.createTempFile("JDBBackup", ".gz").toFile();
		if(!FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
			// On Posix compliant systems, java create tmp files with read/write rights only for user
			// Let do the same on non Posix systems
			final boolean readUserOnly = tmpFile.setReadable(true, true);
			final boolean writeUserOnly = tmpFile.setWritable(true, true);
			if (! (readUserOnly && writeUserOnly)) {
				LoggerFactory.getLogger(getClass()).warn("Fail to apply security restrictions on temporary file. Restrict read to user: {}, restrict write to user: {}", readUserOnly, writeUserOnly);
			}
			if (tmpFile.setExecutable(false, false)) {
				LoggerFactory.getLogger(getClass()).debug("Impossible to set temporary file not executable on this system");
			}
		}
		tmpFile.deleteOnExit();
		return tmpFile;
	}
	
	private <T> String backup(Options options, DestinationManager<T> manager, Destination destination, File tmpFile) throws IOException {
		manager.setProxy(options);
		T destFile = manager.setDestinationPath(destination.getPath());
		getDBSaver(options.getDbType()).save(options, tmpFile);
		return manager.send(tmpFile, destFile);
	}
	
	protected <T> DestinationManager<T> getDestinationManager(Destination destination) {
		@SuppressWarnings("unchecked")
		final DestinationManager<T> manager = (DestinationManager<T>) MANAGERS.get(destination.getProtocol());
		if (manager==null) {
			throw new IllegalArgumentException("Unknown protocol: "+destination.getProtocol());
		}
		return manager;
	}
	
	protected DBDumper getDBSaver(String dbType) {
		final DBDumper saver = SAVERS.get(dbType);
		if (saver==null) {
			throw new IllegalArgumentException("Unknown database type: "+dbType);
		}
		return saver;
	}
}
