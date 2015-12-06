package com.fathzer.jdbbackup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class DropBoxArchiver {
	private static final DbxRequestConfig DBX_CONFIG = new DbxRequestConfig("jDbBackup", Locale.getDefault().toString());

	public static void save() throws DbxException {
		// TODO Auto-generated constructor stub
		String token = "aHAabZt7_ZAAAAAAAAAB3q16ll-SpQfSZ6k_W6SG_cvDNdiO6VPNxj1Aaw7z9rpN";
		DbxClient client = new DbxClient(DBX_CONFIG, token);
		System.out.println("Linked account: " + client.getAccountInfo().displayName);
	}

	public static void main(String[] args) {
		try {
			save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getToken() {
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(DBX_CONFIG, getDbxAppInfo());
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
