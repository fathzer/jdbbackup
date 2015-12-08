package com.fathzer.jdbbackup;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

public class JDbBackup {
	private CmdLineParser parser;
	private Options options;
	
	private JDbBackup() {
		options = new Options();
		parser = new CmdLineParser(options);
	}

	public static void main(String[] args) {
		JDbBackup backup = new JDbBackup();
		try {
			backup.doIt(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java "+JDbBackup.class.getName()+" [options...] "+getArguments(backup.parser));
            // print the list of available options
            backup.parser.printUsage(System.err);
//            System.err.println();
// print option sample. This is useful some time
//            System.err.println("Example: java "+JDbBackup.class.getName()+parser.printExample(OptionHandlerFilter.PUBLIC)+" "+getArguments(parser));
        }
	}

	private void doIt(String[] args) throws CmdLineException {
		// parse the arguments.
		parser.parseArgument(args);
		String fName = options.getFileName();
		if (options.getFormat()!=null) {
			try {
				fName = fName + new SimpleDateFormat(options.getFormat()).format(new Date());
			} catch (IllegalArgumentException e) {
				throw new CmdLineException(parser, "dateFormat is invalid", e);
			}
		}
		System.out.println (fName);
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
