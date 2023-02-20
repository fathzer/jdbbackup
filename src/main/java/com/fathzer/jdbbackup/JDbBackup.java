package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
        } catch (InvalidArgument e) {
            err(e.getMessage());
            // Create a new parser in order to not have currently parsed options displayed as default.
            CmdLineParser p = new CmdLineParser(new Options());
            err("java "+JDbBackup.class.getName()+" [options...] "+getArguments(p));
            // print the list of available options
            p.printUsage(System.err);
        }
	}

	private void doIt(String[] args) throws InvalidArgument {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch(CmdLineException e) {
			throw new InvalidArgument(e);
		}
		try {
			out(backup(options));
		} catch (IOException e) {
        	err("An error occurred while using arguments "+Arrays.toString(args));
        	e.printStackTrace();
        }
	}
	
	public String backup(Options options) throws InvalidArgument, IOException {
		try {
			final Destination destination = new Destination(options.getDestination());
			final DestinationManager manager = getDestinationManager(destination);
			manager.setProxy(options);
			File destFile = manager.setDestinationPath(destination.getPath());
			destFile = new MySQLSaver().save(options, destFile);
			return destFile==null ? null : manager.send(destFile);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgument(e.getMessage());
		}
	}
	
	private DestinationManager getDestinationManager(Destination destination) throws InvalidArgument {
		Reflections reflections = new Reflections("");
		Set<Class<? extends DestinationManager>> classes = reflections.getSubTypesOf(DestinationManager.class);
		for (Class<? extends DestinationManager> implClass : classes) {
			DestinationManager candidate;
			try {
				candidate = implClass.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
			if (candidate.getProtocol().equals(destination.getType())) {
				return candidate;
			}
		}
		throw new InvalidArgument("Unknown protocol: "+destination.getType());
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
