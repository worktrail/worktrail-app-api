package net.worktrail.hub.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import net.worktrail.hub.sync.git.PropertySyncStorage;
import net.worktrail.hub.sync.git.SyncStorage;
import net.worktrail.hub.sync.response.CreateAuthResponse;
import net.worktrail.hub.sync.response.HubEntry;
import net.worktrail.hub.sync.response.RequestErrorException;

public abstract class WorkTrailCliFramework {
	private static final String STORE_APPKEY = "appkey";
	private static final String STORE_SECRETAPIKEY = "secretapikey";
	private static final String STORE_AUTHTOKEN = "authtoken";
	@SuppressWarnings("unused")
	private static final String STORE_REQUESTKEY = "requestkey";
	private SyncStorage storage;
	private WorkTrailAuth auth;
	
	public WorkTrailCliFramework() {
		init(createStorage());
	}
	
	/**
	 * a simple name we use in filenames .. e.g. gitsync
	 */
	protected abstract String getSyncUnixName();
	
	protected SyncStorage createStorage() {
		String propertiesBaseName = getSyncUnixName();
		return new PropertySyncStorage(new File(propertiesBaseName+".properties"), new File(propertiesBaseName+".save.properties"));
	}

	protected void init(SyncStorage syncStorage) {
		this.storage = syncStorage;
		auth = new WorkTrailAuth(storage.getString(STORE_APPKEY), storage.getString(STORE_SECRETAPIKEY), storage.getString(STORE_AUTHTOKEN));

	}
	
	protected void executeFromCommandline(String[] args) {
		if (args.length < 1) {
			System.err.println("Required arguments: {config|import|employees} <Path to git repository>");
			System.exit(1);
		}
		
		if (!hasAuthentication() || "config".equals(args[0])) {
			authenticate();
		} else if ("employees".equals(args[0])) {
			debugEmployees();
		} else if ("import".equals(args[0])) {
			runSync(args);
		} else if ("clean".equals(args[0])) {
			clean();
		}
		
		storage.close();

	}
	

	private void runSync(String[] args) {
		WorkTrailSync sync = createSyncObject(storage, auth, args);
		try {
			sync.prepareHubSync();
			List<HubEntry> toCreate = sync.startHubSync();
			sync.finishHubSync(toCreate);
		} catch (Exception e) {
			throw new RuntimeException("Error while running hub sync", e);
		}
	}

	private void clean() {
		auth.cleanHubEntries();
		storage.cleanSyncedObjects();
	}

	private void debugEmployees() {
		try {
			auth.fetchEmployees();
		} catch (RequestErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			
			auth = new WorkTrailAuth(storage.getString(STORE_APPKEY), storage.getString(STORE_SECRETAPIKEY), storage.getString(STORE_AUTHTOKEN));
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
	
	protected abstract WorkTrailSync createSyncObject(SyncStorage storage, WorkTrailAuth auth, String[] args);
}
