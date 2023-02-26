package com.fathzer.jdbbackup;

import static com.fathzer.jdbbackup.DestinationManager.URI_PATH_SEPARATOR;

class Destination {
	private String type;
	private String path;
	
	public Destination(String dest) {
		if (dest==null) {
			throw new IllegalArgumentException();
		}
		int index = dest.indexOf(':');
		if (index<=0) {
			throw new IllegalArgumentException ("Destination type is missing in "+dest);
		}
		this.type = dest.substring(0, index);
		for (int i=1;i<=2;i++) {
			if (index+i>=dest.length() || dest.charAt(index+i)!=URI_PATH_SEPARATOR) {
				throw new IllegalArgumentException("Destination has not the right format: "+dest+" does not not match type://path");
			}
		}
		this.path = dest.substring(index+3);
		if (this.path.isEmpty()) {
			throw new IllegalArgumentException ("Destination path is missing in "+dest);
		}
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
}
