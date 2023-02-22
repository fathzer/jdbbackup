package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

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
        } catch (InvalidArgumentException e) {
            err(e.getMessage());
            // Create a new parser in order to not have currently parsed options displayed as default.
            CmdLineParser p = new CmdLineParser(new Options());
            err("java "+JDbBackup.class.getName()+" [options...] "+getArguments(p));
            // print the list of available options
            p.printUsage(System.err);
        }
	}

	private void doIt(String[] args) throws InvalidArgumentException {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch(CmdLineException e) {
			throw new InvalidArgumentException(e);
		}
		try {
			out(backup(options));
		} catch (IOException e) {
        	err("An error occurred while using arguments "+Arrays.toString(args));
        	e.printStackTrace();
        }
	}
	
	public String backup(Options options) throws InvalidArgumentException, IOException {
		final Destination destination = new Destination(options.getDestination());
		final DestinationManager<?> manager = getDestinationManager(destination);
		final File tmpFile = File.createTempFile("DBDump", ".gz");
		try {
			tmpFile.deleteOnExit();
			return backup(options, manager, destination, tmpFile);
		} finally {
			Files.delete(tmpFile.toPath());
		}
	}
	
	private <T> String backup(Options options, DestinationManager<T> manager, Destination destination, File tmpFile) throws InvalidArgumentException, IOException {
		try {
			manager.setProxy(options);
			T destFile = manager.setDestinationPath(destination.getPath());
			new MySQLSaver().save(options, tmpFile);
			return manager.send(tmpFile, destFile);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgumentException(e.getMessage());
		}
		
	}
	
	private DestinationManager<?> getDestinationManager(Destination destination) throws InvalidArgumentException {
		final Reflections reflections = new Reflections("");
		
		final Set<Class<? extends DestinationManager>> classes = reflections.getSubTypesOf(DestinationManager.class);
		for (Class<? extends DestinationManager> implClass : classes) {
			final DestinationManager<?> candidate;
			try {
				candidate = implClass.getConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new DestinationManagerInstantiationException(e);
			}
			if (candidate.getProtocol().equals(destination.getType())) {
				return candidate;
			}
		}
		throw new InvalidArgumentException("Unknown protocol: "+destination.getType());
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
}
