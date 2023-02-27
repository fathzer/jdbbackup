package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Set;
import java.util.function.Predicate;

import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

public class JDbBackup {
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
		final DestinationManager<T> manager = findClass(DestinationManager.class, c -> c.getProtocol().equals(destination.getType()));
		if (manager==null) {
			throw new IllegalArgumentException("Unknown protocol: "+destination.getType());
		}
		return manager;
	}
	
	protected DBSaver getDBSaver(String dbType) {
		final DBSaver saver = findClass(DBSaver.class, c -> c.getDBType().equals(dbType));
		if (saver==null) {
			throw new IllegalArgumentException("Unknown database type: "+dbType);
		}
		return saver;
	}

	private <T> T findClass(Class<T> aClass, Predicate<T> filter) {
		final Reflections reflections = new Reflections("com.fathzer.jdbbackup");
		final Set<Class<? extends T>> classes = reflections.getSubTypesOf(aClass);
		for (Class<? extends T> implClass : classes) {
			if (!Modifier.isAbstract(implClass.getModifiers())) {
				final T candidate;
				try {
					candidate = implClass.getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new DestinationManagerInstantiationException(e);
				}
				if (filter.test(candidate)) {
					return candidate;
				}
			}
		}
		return null;
	}
}
