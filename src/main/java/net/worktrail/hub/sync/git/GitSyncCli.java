package net.worktrail.hub.sync.git;

import java.io.File;

public class GitSyncCli {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("We require only one argument: <Path to git repository>");
			System.exit(1);
		}
		
		new GitSync(new File(args[0])).syncLogs();
	}
}
