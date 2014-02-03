package net.worktrail.hub.sync;

import java.util.Arrays;

import net.worktrail.hub.sync.activitystreams.ActivityStreamCli;
import net.worktrail.hub.sync.git.GitSyncCli;

public class Launcher {
	public static void main(String[] args) {
		if (args.length < 1) {
			throw new RuntimeException("Please specify which application to run: git or activitystreams");
		}
		// strip the first argument from array.
		String[] subargs = Arrays.copyOfRange(args, 1, args.length);
		if ("git".equals(args[0])) {
			GitSyncCli.main(subargs);
		} else if ("activitystreams".equals(args[0])) {
			ActivityStreamCli.main(subargs);
		} else {
			throw new RuntimeException("Invalid argument: {" + args[0] + "}");
		}
	}
}
