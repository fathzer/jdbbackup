package com.fathzer.jdbbackup.managers.dropbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.function.Supplier;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.http.StandardHttpRequestor.Config;
import com.fathzer.jdbbackup.utils.ProxySettings;

/** Common component between {@link com.fathzer.jdbbackup.managers.dropbox.DropBoxManager} and {@link com.fathzer.jdbbackup.managers.dropbox.DropBoxTokenCmd}
 */
public class DropBoxBase {
	static final String REFRESH_PREFIX = "refresh-";
	private static final String NAME = "jDbBackup";
	static Supplier<DbxAppInfo> dbxAppInfoProvider = () -> {
		try (InputStream in = DropBoxBase.class.getResourceAsStream("keys.properties")) {
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
	
	DbxRequestConfig config;

	public void setProxy(final ProxySettings options) {
		Config.Builder builder = Config.builder();
		if (options.getHost()!=null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(options.getHost(),options.getPort()));
			if (options.getLogin() != null) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(options.getLogin().getUser(), options.getLogin().getPassword().toCharArray());
					}
				});
			}
			builder.withProxy(proxy);
		}
		DbxRequestConfig.Builder rbuilder = DbxRequestConfig.newBuilder(NAME);
		rbuilder.withHttpRequestor(new StandardHttpRequestor(builder.build()));
		this.config = rbuilder.build();
	}

	/** Sets the supplier of Dropbox application's credentials.
	 * <br>By default, the library uses the jdbbackup application's credential stored in keys.properties resource file.
	 * <br>You can switch to another application of your choice by passing another supplier to this method.
	 * @param dbxAppInfoProvider The new application credentials supplier
	 */
	public static void setDbxAppInfoSupplier(Supplier<DbxAppInfo> dbxAppInfoProvider) {
		DropBoxBase.dbxAppInfoProvider = dbxAppInfoProvider;
	}
}
