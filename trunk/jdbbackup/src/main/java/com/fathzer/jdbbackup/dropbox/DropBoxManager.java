package com.fathzer.jdbbackup.dropbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.kohsuke.args4j.CmdLineException;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
import com.dropbox.core.http.StandardHttpRequestor;
import com.fathzer.jdbbackup.FileManager;
import com.fathzer.jdbbackup.ProxyParameters;

public class DropBoxManager implements FileManager {
	private static final String NAME = "jDbBackup";
	private DbxRequestConfig config;
	private String token;
	private String path;
	
	public DropBoxManager() {
		this.config = new DbxRequestConfig(NAME, Locale.getDefault().toString());
	}

	public DropBoxManager(final ProxyParameters params) {
		Proxy proxy = Proxy.NO_PROXY;
		if (params.getAddress()!=null) {
	        proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(params.getAddress(),params.getPort()));
			if (params.getUser() != null) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(params.getUser(), params.getPwd().toCharArray());
					}
				});
			}
		}
		this.config = new DbxRequestConfig(NAME, Locale.getDefault().toString(), new StandardHttpRequestor(proxy));
	}
	
	@Override
	public void send(File file) throws IOException {
		DbxClient client = new DbxClient(config, token);
		try (InputStream in = new FileInputStream(file)) {
			DbxEntry.File entry = client.uploadFile(path, DbxWriteMode.force(), file.length(), in); 
			System.out.println("Sent to Dropbox: "+entry.name+"("+entry.rev+")");
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}

	public static void main(String[] args) {
		try {
			ProxyParameters params = new ProxyParameters("138.21.89.192",3128,"a193041","jma12015"); 
			DropBoxManager archiver = new DropBoxManager(params);
			archiver.getToken();
//			archiver.setToken("aHAabZt7_ZAAAAAAAAAB3q16ll-SpQfSZ6k_W6SG_cvDNdiO6VPNxj1Aaw7z9rpN");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getToken() {
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, getDbxAppInfo());
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Enter the authorization code there:");
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			System.out.println("Please wait ...");
	        DbxAuthFinish authFinish = webAuth.finish(code);
	        String accessToken = authFinish.accessToken;
	        System.out.println("Your token is: "+accessToken);
	        System.out.println("Keep it in a secure place as it allows to access to your yaback folder on Dropbox");
		} catch (Exception e) {
			System.err.println ("Sorry, an error occurred:");
			e.printStackTrace();
		}
	}
	
	private static DbxAppInfo getDbxAppInfo() {
		// For obvious reasons, your application keys and secret are not released with the source files.
		// You should edit keys.properties in order to run this demo
		ResourceBundle bundle = ResourceBundle.getBundle(DropBoxManager.class.getPackage().getName()+".keys"); //$NON-NLS-1$
		String key = bundle.getString("appKey");
		String secret = bundle.getString("appSecret");
		if (key.length()==0 || secret.length()==0) {
			throw new MissingResourceException("App key and secret not provided","","");
		}
		return new DbxAppInfo(key, secret);
	}

	@Override
	public void parseFileName(String fileName) throws CmdLineException {
		int index = fileName.indexOf('/');
		if (index<=0) {
			throw new CmdLineException("Unable to locate token. "+"FileName should conform to the format access_token/path");
		}
		this.token = fileName.substring(0, index);
		this.path = fileName.substring(index+1);
		if (this.path.isEmpty()) {
			throw new CmdLineException("Unable to locate destination path. "+"FileName should conform to the format access_token/path");
		}
		if (!path.startsWith("/")) {
			path = "/"+path;
		}
	}
}
