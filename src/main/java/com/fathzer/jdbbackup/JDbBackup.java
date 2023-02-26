package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;
import org.reflections.Reflections;

public class JDbBackup {
	public JDbBackup() {
		super();
	}
	
	public static void main(String[] args) {
		JDbBackup backup = new JDbBackup();
		try {
			backup.doIt(args);
        } catch (IllegalArgumentException e) {
            err(e.getMessage());
            // Create a new parser in order to not have currently parsed options displayed as default.
            CmdLineParser p = new CmdLineParser(new Options());
            err("java "+JDbBackup.class.getName()+" [options...] "+getArguments(p));
            // print the list of available options
            p.printUsage(System.err);
        }
	}

	private void doIt(String[] args) {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch(CmdLineException e) {
			throw new IllegalArgumentException(e);
		}
		try {
			out(backup(options));
		} catch (IOException e) {
        	err("An error occurred while using arguments "+Arrays.toString(args));
        	err(e);
        }
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

	private static CharSequence getArguments(CmdLineParser parser) {
		StringBuilder builder = new StringBuilder();
		for (OptionHandler<?> arg:parser.getArguments()) {
			if (arg.option.required()) {
				if (builder.length()!=0) {
					builder.append(' ');
				}
				builder.append(arg.option.metaVar());
			}
		}
		return builder;
	}
	
	public static void out(String message) {
		System.out.println(message);
	}
	
	public static void err(String message) {
		System.err.println(message);
	}
	
	public static void err(Throwable e) {
		e.printStackTrace();
	}
}
