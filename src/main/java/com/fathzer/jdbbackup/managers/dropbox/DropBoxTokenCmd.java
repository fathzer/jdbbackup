package com.fathzer.jdbbackup.managers.dropbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.fathzer.jdbbackup.cmd.ProxyOptions;
import com.fathzer.jdbbackup.cmd.JDbBackupCmd;

/** A helper class to obtain a token usable with DropBoxManager
 */
public class DropBoxTokenCmd extends DropBoxBase {

	public static void main(String[] args) throws CmdLineException {
		ProxyOptions options = new ProxyOptions();
		CmdLineParser parser = new CmdLineParser(options);
		parser.parseArgument(args);
		DropBoxTokenCmd archiver = new DropBoxTokenCmd();
		archiver.setProxy(options.toProxySettings());
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
        JDbBackupCmd.out("1. Go to: " + authorizeUrl);
        JDbBackupCmd.out("2. Click \"Allow\" (you might have to log in first)");
        JDbBackupCmd.out("3. Enter the authorization code there:");
		try {
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
			JDbBackupCmd.out("Please wait ...");
	        DbxAuthFinish authFinish = auth.finishFromCode(code);
	        String accessToken = authFinish.getRefreshToken();
	        JDbBackupCmd.out("Your token is: "+REFRESH_PREFIX+accessToken);
	        JDbBackupCmd.out("Keep it in a secure place as it allows to access to your backup folder on Dropbox");
		} catch (Exception e) {
			JDbBackupCmd.err ("Sorry, an error occurred:");
			JDbBackupCmd.err(e);
		}
	}
}
