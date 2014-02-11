package net.worktrail.appapi.git;

import java.io.File;

import net.worktrail.appapi.WorkTrailAppApi;
import net.worktrail.appapi.WorkTrailCliFramework;
import net.worktrail.appapi.WorkTrailSync;

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
			WorkTrailAppApi auth, String[] args) {
		return new GitSync(auth, storage, new File(args[1]));
	}

	@Override
	protected String getSyncUnixName() {
		return "gitsync";
	}
}
