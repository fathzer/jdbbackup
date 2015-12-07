package com.fathzer.jdbbackup;

import java.io.File;
import java.io.IOException;

public class Test {
	
	public static void main(String[] args) {
//		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//		final File destFile = new File("backup-" + database + "-" + params.getDbHost() + "-(" + dateFormat.format(new Date()) + ").sql");
		
		DBParameters params = new DBParameters();
		params.setPwd("gti9220");
		try {
			File file = new DBSaver().save(params, "Photos");
			System.out.println("done! -> "+file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
