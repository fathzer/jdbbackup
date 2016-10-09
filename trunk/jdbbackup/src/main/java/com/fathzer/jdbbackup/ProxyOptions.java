package com.fathzer.jdbbackup;

import org.kohsuke.args4j.Option;

public class ProxyOptions {
	@Option(name="-ph", metaVar="proxyHost", usage = "The proxy host used to send the backup file to its destination")
	private String proxyHost;
	@Option(name="-pp", metaVar="proxyPort", usage = "The proxy port used to send the backup file to its destination", depends={"-ph"})
	private int proxyPort;
	@Option(name="-pu", metaVar="proxyUser", usage = "The proxy user used to send the backup file to its destination", depends={"-ph"})
	private String proxyUser;
	@Option(name="-ppwd", metaVar="proxyPassword", usage = "The proxy password used to send the backup file to its destination", depends={"-pu"})
	private String proxyPwd;
	
	public String getProxyHost() {
		return proxyHost;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	public String getProxyUser() {
		return proxyUser;
	}
	public String getProxyPwd() {
		return proxyPwd;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public void setProxyPwd(String proxyPwd) {
		this.proxyPwd = proxyPwd;
	}
}