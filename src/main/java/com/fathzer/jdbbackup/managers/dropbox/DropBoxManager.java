package com.fathzer.jdbbackup.managers.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.WriteMode;

import com.fathzer.jdbbackup.DefaultPathDecoder;
import com.fathzer.jdbbackup.DestinationManager;

/** A destination manager that saves the backups to a dropbox account.
 * <br>Destination paths have the following format dropbox://<i>token</i>/<i>fileName</i>
 */
public class DropBoxManager extends DropBoxBase implements DestinationManager<DropBoxManager.DropBoxDestination> {

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
}
