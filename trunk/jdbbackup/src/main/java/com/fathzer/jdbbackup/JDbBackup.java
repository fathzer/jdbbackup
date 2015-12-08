package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

import com.fathzer.jdbbackup.dropbox.DropBoxManager;

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
        } catch (IOException e) {
        	//TODO
        	e.printStackTrace();
        }
	}

	private void doIt(String[] args) throws CmdLineException, IOException {
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
		FileManager manager = null;
		File destFile = null;
		if (Options.Target.DROPBOX.equals(options.getTarget())) {
			manager = new DropBoxManager();
			manager.parseFileName(fName);
		} else {
			throw new UnsupportedOperationException("Not yet implemented");
		}
		//TODO catch IOException in order to separate problems during data extract and during saving the extraction
		destFile = new DBSaver().save(options, destFile);
		if (destFile!=null) {
			manager.send(destFile);
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
