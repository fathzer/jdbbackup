package com.fathzer.jdbbackup.cmd;

import java.net.URI;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

class Options extends ProxyOptions {
	@Option(name = "-db", metaVar="dbType", usage = "Data base url (for example mysql://user:pwd@host:port/db")
    private URI db;

    @Argument(index = 1, metaVar="dest", usage ="Destination (example sftp://user:pwd@host/filepath)", required=true)
    private String dest;

	public URI getDbURI() {
		return db;
	}
	
	public void setDbURI(URI db) {
		this.db = db;
	}

	public String getDestination() {
		return dest;
	}
	
	public void setDestination(String destination) {
		this.dest = destination;
	}
}
