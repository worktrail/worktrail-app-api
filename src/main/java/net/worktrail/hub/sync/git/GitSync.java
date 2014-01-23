package net.worktrail.hub.sync.git;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitSync {

	private Git git;

	public GitSync(File gitRepository) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(new File(gitRepository, ".git"))
				.readEnvironment()
				.findGitDir()
				.build();
			git = new Git(repository);
		} catch (IOException e) {
			throw new RuntimeException("Error while creating file repository.", e);
		}
	}
	
	public void syncLogs() {
		try {
			Iterable<RevCommit> logs;
			logs = git.log().all().call();
			for (RevCommit rev : logs) {
				
				System.out.println("rev: " + rev + " - " + rev.getShortMessage() + " - " + new Date(rev.getCommitTime() * 1000L));
			}
		} catch (GitAPIException | IOException e) {
			throw new RuntimeException("Error while syncing logs.", e);
		}
	}

}
