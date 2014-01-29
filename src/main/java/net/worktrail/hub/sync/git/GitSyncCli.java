package net.worktrail.hub.sync.git;

import java.io.File;

import net.worktrail.hub.sync.WorkTrailAuth;
import net.worktrail.hub.sync.WorkTrailCliFramework;
import net.worktrail.hub.sync.WorkTrailSync;

public class GitSyncCli extends WorkTrailCliFramework {
	
	public GitSyncCli() {
	}
	
	public static void main(String[] args) {
		new GitSyncCli().executeFromCommandline(args);
	}

//	private void runSync(File file) {
//		gitSync.syncLogs();
//	}

	@Override
	protected WorkTrailSync createSyncObject(SyncStorage storage,
			WorkTrailAuth auth, String[] args) {
		return new GitSync(auth, storage, new File(args[1]));
	}
}
