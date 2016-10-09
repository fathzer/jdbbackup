package com.fathzer.jdbbackup;

public class Destination {
	private String type;
	private String path;
	
	public Destination(String dest) {
		int index = dest.indexOf(':');
		if (index<=0) {
			throw new IllegalArgumentException ("Destination type is missing in "+dest);
		} else if (index==dest.length()-1) {
			throw new IllegalArgumentException ("Destination path is missing in "+dest);
		}
		this.type = dest.substring(0, index);
		this.path = dest.substring(index+1);
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
}
