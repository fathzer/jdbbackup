package com.fathzer.jdbbackup.managers;

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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.kohsuke.args4j.CmdLineParser;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.http.StandardHttpRequestor.Config;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;
import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.InvalidArgument;
import com.fathzer.jdbbackup.PathDecoder;
import com.fathzer.jdbbackup.ProxyOptions;

/** A destination manager that saves the backups to a dropbox account.
 */
public class DropBoxManager implements DestinationManager {
	private static final String NAME = "jDbBackup";
	private DbxRequestConfig config;
	private String token;
	private String path;
	
	/** Constructor.
	 */
	public DropBoxManager() {
		super();
		this.config = new DbxRequestConfig(NAME);
	}

	public DropBoxManager(final ProxyOptions options) {
		super();
		Config.Builder builder = Config.builder();
		if (options.getProxyHost()!=null) {
	        Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(options.getProxyHost(),options.getProxyPort()));
			if (options.getProxyUser() != null) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(options.getProxyUser(), options.getProxyPwd().toCharArray());
					}
				});
			}
			builder.withProxy(proxy);
		}
		DbxRequestConfig.Builder rbuilder = DbxRequestConfig.newBuilder(NAME);
		rbuilder.withHttpRequestor(new StandardHttpRequestor(builder.build()));
		this.config = rbuilder.build();
	}
	
	@Override
	public String send(File file) throws IOException {
		DbxClientV2 client = new DbxClientV2(config, token);
		try (InputStream in = new FileInputStream(file)) {
			UploadBuilder builder = client.files().uploadBuilder(path);
			builder.withMode(WriteMode.OVERWRITE);
			FileMetadata data = builder.uploadAndFinish(in, file.length());
			return "Sent to Dropbox: "+data.getPathDisplay()+" (rev: "+data.getRev()+")";
		} catch (DbxException e) {
			throw new IOException(e);
		} finally {
			file.delete();
		}
	}
	
	@Override
	public File setDestinationPath(String fileName) throws InvalidArgument {
		int index = fileName.indexOf('/');
		if (index<=0) {
			throw new InvalidArgument("Unable to locate token. "+"FileName should conform to the format access_token/path");
		}
		this.token = fileName.substring(0, index);
		this.path = fileName.substring(index+1);
		if (this.path.isEmpty()) {
			throw new InvalidArgument("Unable to locate destination path. Path should conform to the format access_token/path");
		}
		if (!path.startsWith("/")) {
			path = "/"+path;
		}
		path = getPathDecoder().decodePath(path);
		return null;
	}
	
	private void getToken() {
	    DbxAppInfo appInfo = getDbxAppInfo();
	    DbxWebAuth auth = new DbxWebAuth(config, appInfo);
	    DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
	             .withNoRedirect()
	             .build();
        String authorizeUrl = auth.authorize(authRequest);
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Enter the authorization code there:");
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			System.out.println("Please wait ...");
	        DbxAuthFinish authFinish = auth.finishFromCode(code);
	        String accessToken = authFinish.getAccessToken();
	        System.out.println("Your token is: "+accessToken);
	        System.out.println("Keep it in a secure place as it allows to access to your backup folder on Dropbox");
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
	public PathDecoder getPathDecoder() {
		return new DefaultPathDecoder();
	}

	public static void main(String[] args) {
		try {
			ProxyOptions options = new ProxyOptions();
			CmdLineParser parser = new CmdLineParser(options);
			parser.parseArgument(args);
			DropBoxManager archiver = new DropBoxManager(options);
			archiver.getToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getProtocol() {
		return "dropbox";
	}
}
