package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

import com.fathzer.jdbbackup.dropbox.DropBoxManager;

public class JDbBackup {
	
	public JDbBackup() {
		super();
	}
	
	public static void main(String[] args) {
		JDbBackup backup = new JDbBackup();
		try {
			backup.doIt(args);
        } catch (InvalidArgument e) {
            System.err.println(e.getMessage());
            // Create a new parser in order to not have currently parsed options displayed as default.
            CmdLineParser p = new CmdLineParser(new Options());
            System.err.println("java "+JDbBackup.class.getName()+" [options...] "+getArguments(p));
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
			System.out.println(backup(options));
		} catch (IOException e) {
        	System.err.println("An error occurred while using arguments "+Arrays.toString(args));
        	e.printStackTrace();
        }
	}
	
	public String backup(Options options) throws InvalidArgument, IOException {
		try {
			Destination destination = new Destination(options.getDestination());
			DestinationManager manager = getFileManager(destination);
			File destFile = manager.setDestinationPath(destination.getPath());
			destFile = new DBSaver().save(options, destFile);
			return destFile==null ? null : manager.send(destFile);
		} catch (IllegalArgumentException e) {
			throw new InvalidArgument(e.getMessage());
		}
	}
	
	protected DestinationManager getFileManager(Destination destination) throws InvalidArgument {
		if ("dropbox".equals(destination.getType())) {
			return new DropBoxManager();
		} else if ("file".equals(destination.getType())) {
			return new FileManager();
		} else {
			throw new InvalidArgument("Unknown protocol: "+destination.getType());
		}
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
}
