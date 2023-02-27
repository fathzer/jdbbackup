package com.fathzer.jdbbackup;

import java.io.IOException;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

public class JDbBackupCmd {
	public static void main(String[] args) {
		JDbBackupCmd backup = new JDbBackupCmd();
		try {
			backup.doIt(args);
        } catch (IllegalArgumentException e) {
            err(e.getMessage());
            // Create a new parser in order to not have currently parsed options displayed as default.
            CmdLineParser p = new CmdLineParser(new Options());
            err("java "+JDbBackupCmd.class.getName()+" [options...] "+getArguments(p));
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
			out(new JDbBackup().backup(options));
		} catch (IOException e) {
        	err("An error occurred while using arguments "+Arrays.toString(args));
        	err(e);
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
	
	@SuppressWarnings("java:S106")
	public static void out(String message) {
		System.out.println(message);
	}
	
	@SuppressWarnings("java:S106")
	public static void err(String message) {
		System.err.println(message);
	}
	
	@SuppressWarnings("java:S106")
	public static void err(Throwable e) {
		e.printStackTrace();
	}
}
