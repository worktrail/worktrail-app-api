package net.worktrail.hub.sync.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import net.worktrail.hub.sync.WorkTrailAuth;
import net.worktrail.hub.sync.WorkTrailScope;
import net.worktrail.hub.sync.response.CreateAuthResponse;
import net.worktrail.hub.sync.response.RequestErrorException;

public class GitSyncCli {
	private static final String STORE_APPKEY = "appkey";
	private static final String STORE_SECRETAPIKEY = "secretapikey";
	private static final String STORE_AUTHTOKEN = "authtoken";
	private static final String STORE_REQUESTKEY = "requestkey";
	private SyncStorage storage;
	private WorkTrailAuth auth;
	
	
	public GitSyncCli(SyncStorage syncStorage) {
		this.storage = syncStorage;
		auth = new WorkTrailAuth(storage.getString(STORE_APPKEY), storage.getString(STORE_SECRETAPIKEY));
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Required arguments: {config|import} <Path to git repository>");
			System.exit(1);
		}
		
		GitSyncCli cli = new GitSyncCli(new PropertySyncStorage(new File("gitsync.properties")));
		
		if (!cli.hasAuthentication() || "config".equals(args[0])) {
			cli.authenticate();
		}
		
		cli.runSync();
		
		
//		new GitSync(new File(args[0])).syncLogs();
	}

	private void authenticate() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			if (storage.getString(STORE_APPKEY) == null || storage.getString(STORE_SECRETAPIKEY) == null) {
				System.out.print("Please enter your APPKEY: ");
				String appKey = reader.readLine().trim();
				System.out.print("Please enter your secret API Key: ");
				String secretApiKey = reader.readLine().trim();
				storage.setString(STORE_APPKEY, appKey);
				storage.setString(STORE_SECRETAPIKEY, secretApiKey);
			}
			
			auth = new WorkTrailAuth(storage.getString(STORE_APPKEY), storage.getString(STORE_SECRETAPIKEY));
			CreateAuthResponse authRequest = auth.createAuthRequest(new WorkTrailScope[] {
					WorkTrailScope.READ_EMPLOYEES, WorkTrailScope.SYNC_HUB_DATA });
			storage.setString(STORE_AUTHTOKEN, authRequest.getAuthToken());
			URL redirectUrl = authRequest.getRedirectUrl();
			System.out.println("Please open the following URL in your Browser and authorize this app:");
			System.out.println(redirectUrl.toString());
			System.out.print("Waiting for authorization");
			
			while (true) {
				Thread.sleep(1000);
				if (auth.verifyAuthorization(authRequest.getRequestKey())) {
					System.out.println();
					System.out.println("Thanks for the authorization!");
					break;
				} else {
					System.out.print(".");
				}
			}
		} catch (IOException | RequestErrorException | InterruptedException e) {
			throw new RuntimeException("Error while configuring app key.", e);
		}
	}

	private boolean hasAuthentication() {
		return storage.getString(STORE_APPKEY) != null
				&& storage.getString(STORE_SECRETAPIKEY) != null
				&& storage.getString(STORE_AUTHTOKEN) != null;
	}

	private void runSync() {
	}
}
