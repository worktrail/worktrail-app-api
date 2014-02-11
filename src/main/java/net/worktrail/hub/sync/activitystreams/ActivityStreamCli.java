package net.worktrail.hub.sync.activitystreams;

import net.worktrail.hub.sync.WorkTrailAppApi;
import net.worktrail.hub.sync.WorkTrailCliFramework;
import net.worktrail.hub.sync.WorkTrailSync;
import net.worktrail.hub.sync.git.SyncStorage;

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
