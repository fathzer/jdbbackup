package com.fathzer.jdbbackup.dropbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.http.StandardHttpRequestor;
import com.fathzer.jdbbackup.ProxyParameters;

public class DropBoxArchiver {
	private static final String NAME = "jDbBackup";
	private DbxRequestConfig config;
	private String token;
	
	public DropBoxArchiver() {
		this.config = new DbxRequestConfig(NAME, Locale.getDefault().toString());
	}

	public DropBoxArchiver(final ProxyParameters params) {
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
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void save() throws DbxException {
		// TODO Auto-generated
		DbxClient client = new DbxClient(config, token);
		System.out.println("Linked account: " + client.getAccountInfo().displayName);
	}

	public static void main(String[] args) {
		try {
			ProxyParameters params = new ProxyParameters("138.21.89.192",3128,"a193041","jma12015"); 
			DropBoxArchiver archiver = new DropBoxArchiver(params);
			archiver.getToken();
//			archiver.setToken("aHAabZt7_ZAAAAAAAAAB3q16ll-SpQfSZ6k_W6SG_cvDNdiO6VPNxj1Aaw7z9rpN");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getToken() {
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
		ResourceBundle bundle = ResourceBundle.getBundle(DropBoxArchiver.class.getPackage().getName()+".keys"); //$NON-NLS-1$
		String key = bundle.getString("appKey");
		String secret = bundle.getString("appSecret");
		if (key.length()==0 || secret.length()==0) {
			throw new MissingResourceException("App key and secret not provided","","");
		}
		return new DbxAppInfo(key, secret);
	}
}
