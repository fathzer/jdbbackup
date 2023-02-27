package com.fathzer.jdbbackup.cmd;

import org.kohsuke.args4j.Option;

import com.fathzer.jdbbackup.utils.ProxySettings;

public class ProxyOptions {
	@Option(name="-p", metaVar="proxy", usage = "The proxy used for the backup, format is [user[:pwd]@]host:port")
	private String proxy;

	public ProxySettings toProxySettings() {
		return ProxySettings.fromString(proxy);
	}
}
