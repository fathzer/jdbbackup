package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

import com.fathzer.jdbbackup.dropbox.DropBoxManager;

public class JDbBackup {
	protected CmdLineParser parser;
	protected Options options;
	protected PathDecoder pathDecoder;
	
	private JDbBackup() {
		options = new Options();
		parser = new CmdLineParser(options);
		pathDecoder = new DefaultPathDecoder();
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
        } catch (IOException e) {
        	//TODO
        	e.printStackTrace();
        }
	}

	private void doIt(String[] args) throws InvalidArgument, IOException {
		try {
			// parse the arguments.
			parser.parseArgument(args);
		} catch(CmdLineException e) {
			throw new InvalidArgument(e);
		}
		DestinationManager manager = getFileManager();
		File destFile = manager.setDestinationPath(options.getFileName());
		//TODO catch IOException in order to separate problems during data extract and during saving the extraction
		destFile = new DBSaver().save(options, destFile);
		if (destFile!=null) {
			manager.send(destFile);
		}
	}
	
	protected DestinationManager getFileManager() throws InvalidArgument {
		if ("dropbox".equals(options.getTarget())) {
			return new DropBoxManager(pathDecoder);
		} else if ("file".equals(options.getTarget())) {
			return new FileManager(pathDecoder);
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
