package net.worktrail.appapi.activitystreams;

import net.worktrail.appapi.WorkTrailAppApi;
import net.worktrail.appapi.WorkTrailCliFramework;
import net.worktrail.appapi.WorkTrailSync;
import net.worktrail.appapi.git.SyncStorage;

public class ActivityStreamCli extends WorkTrailCliFramework {

	@Override
	protected WorkTrailSync createSyncObject(SyncStorage storage,
			WorkTrailAppApi auth, String[] args) {
		return new ActivityStreamSync(storage, auth, args);
	}

	public static void main(String[] args) {
		new ActivityStreamCli().executeFromCommandline(args);
	}

	@Override
	protected String getSyncUnixName() {
		return "activitystreamsync";
	}
}
