package com.fathzer.jdbbackup;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;

public class JDbBackup {

	public static void main(String[] args) {
		CmdLineParser parser = new CmdLineParser(new Options());
        try {
            // parse the arguments.
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java "+JDbBackup.class.getName()+" [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.print("Example: java "+JDbBackup.class.getName()+parser.printExample(OptionHandlerFilter.PUBLIC));
            System.err.println(" "+parser.getArguments().get(0).option.metaVar());
        }
	}

}
