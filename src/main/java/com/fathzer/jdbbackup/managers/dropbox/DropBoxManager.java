package com.fathzer.jdbbackup.managers.dropbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.function.Supplier;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.http.StandardHttpRequestor.Config;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;
import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.JDbBackup;
import com.fathzer.jdbbackup.ProxyOptions;

/** A destination manager that saves the backups to a dropbox account.
 * <br>Destination paths have the foolowing format dropbox://<i>token</i>/<i>fileName</i>
 */
public class DropBoxManager implements DestinationManager<DropBoxManager.DropBoxDestination> {
	private static final String REFRESH_PREFIX = "refresh-";
	private static final String NAME = "jDbBackup";
	private static Supplier<DbxAppInfo> dbxAppInfoProvider = () -> {
		try (InputStream in = DropBoxManager.class.getResourceAsStream("keys.properties")) {
			final Properties properties = new Properties();
			properties.load(in);
			String key = properties.getProperty("appKey");
			String secret = properties.getProperty("appSecret");
			if (key.length()==0 || secret.length()==0) {
				throw new MissingResourceException("App key and secret not provided","","");
			}
			return new DbxAppInfo(key, secret);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	};
	
	private DbxRequestConfig config;

	static class DropBoxDestination {
		private String token;
		private String path;
	}
	
	/** Constructor.
	 */
	public DropBoxManager() {
		super();
	}

	@Override
	public void setProxy(final ProxyOptions options) {
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
	public String send(final File file, DropBoxDestination dest) throws IOException {
		DbxClientV2 client = new DbxClientV2(config, getCredential(dest.token));
		try (InputStream in = new FileInputStream(file)) {
			UploadBuilder builder = client.files().uploadBuilder(dest.path);
			builder.withMode(WriteMode.OVERWRITE);
			FileMetadata data = builder.uploadAndFinish(in, file.length());
			return "Sent to Dropbox: "+data.getPathDisplay()+" (rev: "+data.getRev()+")";
		} catch (DbxException e) {
			throw new IOException(e);
		}
	}
	
	private DbxCredential getCredential(String token) {
		if (token.startsWith(REFRESH_PREFIX)) {
			final DbxAppInfo info = dbxAppInfoProvider.get();
			return new DbxCredential("fake", 0L, token.substring(REFRESH_PREFIX.length()), info.getKey(), info.getSecret());
		} else {
			return new DbxCredential(token);
		}
	}
	
	@Override
	public DropBoxDestination setDestinationPath(final String fileName) {
		int index = fileName.indexOf(URI_PATH_SEPARATOR);
		if (index<=0) {
			throw new IllegalArgumentException("Unable to locate token. "+"FileName should conform to the format access_token/path");
		}
		DropBoxDestination dest = new DropBoxDestination();
		dest.token = fileName.substring(0, index);
		dest.path = fileName.substring(index+1);
		if (dest.path.isEmpty()) {
			throw new IllegalArgumentException("Unable to locate destination path. Path should conform to the format access_token/path");
		}
		if (dest.path.charAt(0)!=URI_PATH_SEPARATOR) {
			dest.path = URI_PATH_SEPARATOR+dest.path;
		}
		dest.path = DefaultPathDecoder.INSTANCE.decodePath(dest.path);
		return dest;
	}

	@Override
	public String getProtocol() {
		return "dropbox";
	}

	/** Sets the supplier of Dropbox application's credentials.
	 * <br>By default, the library uses the jdbbackup application's credential stored in keys.properties resource file.
	 * <br>You can switch to another application of your choice by passing another supplier to this method.
	 * @param dbxAppInfoProvider The new application credentials supplier
	 */
	public static void setDbxAppInfoSupplier(Supplier<DbxAppInfo> dbxAppInfoProvider) {
		DropBoxManager.dbxAppInfoProvider = dbxAppInfoProvider;
	}

	public static void main(String[] args) throws CmdLineException {
		ProxyOptions options = new ProxyOptions();
		CmdLineParser parser = new CmdLineParser(options);
		parser.parseArgument(args);
		DropBoxManager archiver = new DropBoxManager();
		archiver.setProxy(options);
		archiver.getToken();
	}

	private void getToken() {
	    DbxAppInfo appInfo = dbxAppInfoProvider.get();
	    DbxWebAuth auth = new DbxWebAuth(config, appInfo);
	    DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
	             .withNoRedirect()
	             .withTokenAccessType(TokenAccessType.OFFLINE)
	             .build();
        String authorizeUrl = auth.authorize(authRequest);
        JDbBackup.out("1. Go to: " + authorizeUrl);
        JDbBackup.out("2. Click \"Allow\" (you might have to log in first)");
        JDbBackup.out("3. Enter the authorization code there:");
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			JDbBackup.out("Please wait ...");
	        DbxAuthFinish authFinish = auth.finishFromCode(code);
	        String accessToken = authFinish.getRefreshToken();
	        JDbBackup.out("Your token is: "+REFRESH_PREFIX+accessToken);
	        JDbBackup.out("Keep it in a secure place as it allows to access to your backup folder on Dropbox");
		} catch (Exception e) {
			JDbBackup.err ("Sorry, an error occurred:");
			JDbBackup.err(e);
		}
	}
}
