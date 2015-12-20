package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

import com.fathzer.jdbbackup.dropbox.DropBoxManager;

public class JDbBackup {
	protected CmdLineParser parser;
	protected Options options;
	
	private JDbBackup() {
		options = new Options();
		parser = new CmdLineParser(options);
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
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch(CmdLineException e) {
			throw new InvalidArgument(e);
		}
		DestinationManager manager = getFileManager();
		File destFile = manager.setDestinationPath(options.getFileName());
		try {
			destFile = new DBSaver().save(options, destFile);
			if (destFile!=null) {
				manager.send(destFile);
			}
		} catch (IOException e) {
        	System.err.println("An error occurred while using arguments "+Arrays.toString(args));
        	e.printStackTrace();
        }
	}
	
	protected DestinationManager getFileManager() throws InvalidArgument {
		if ("dropbox".equals(options.getTarget())) {
			return new DropBoxManager();
		} else if ("file".equals(options.getTarget())) {
			return new FileManager();
		} else {
			throw new InvalidArgument("Unknown target: "+options.getTarget());
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
