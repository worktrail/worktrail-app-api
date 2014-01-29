package net.worktrail.hub.sync.git;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.worktrail.hub.sync.EmployeeListResponse;
import net.worktrail.hub.sync.WorkTrailAuth;
import net.worktrail.hub.sync.response.CreateHubEntriesResponse;
import net.worktrail.hub.sync.response.Employee;
import net.worktrail.hub.sync.response.HubEntry;
import net.worktrail.hub.sync.response.RequestErrorException;
import net.worktrail.hub.sync.response.SrcType;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitSync {

	private Git git;
	private WorkTrailAuth auth;
	private SyncStorage storage;
	private Properties props;
	private String urlPrefix;
	private String projectName;

	public GitSync(WorkTrailAuth auth, SyncStorage storage, File gitRepository) {
		this.auth = auth;
		this.storage = storage;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			readProperties(gitRepository);
			Repository repository = builder.setGitDir(new File(gitRepository, ".git"))
				.readEnvironment()
				.findGitDir()
				.build();
			git = new Git(repository);
		} catch (IOException e) {
			throw new RuntimeException("Error while creating file repository.", e);
		}
	}
	
	private void readProperties(File gitRepository) throws FileNotFoundException, IOException {
		File workTrailProperties = new File(gitRepository, ".worktrail.properties");
		if (!workTrailProperties.exists()) {
			throw new RuntimeException("No .worktrail.properties file found. Please create one!");
		}
		props = new Properties();
		props.load(new FileInputStream(workTrailProperties));
		
		urlPrefix = props.getProperty("urlprefix");
		projectName = props.getProperty("projectName");
		if (projectName == null) {
			projectName = gitRepository.getName();
		}
	}

	public void syncLogs() {
		// First fetch all employees
		try {
			EmployeeListResponse employeeListResponse = auth.fetchEmployees();
			Map<String, Employee> employeeEmailMap = new HashMap<>();
			for (Employee employee : employeeListResponse.getEmployeeList()) {
				employeeEmailMap.put(employee.getPrimaryEmail(), employee);
				String emailAliases = storage.getString("employee.emailaddresses." + employee.getEmployeeId());
				if (emailAliases == null) {
					storage.setString("employee.emailaddresses." + employee.getEmployeeId(), employee.getPrimaryEmail());
				} else {
					for (String alias : emailAliases.split(",")) {
						alias = alias.trim();
						if (alias.length() > 0) {
							employeeEmailMap.put(alias, employee);
						}
					}
				}
			}
			
			Iterable<RevCommit> logs;
			logs = git.log().all().call();
			List<HubEntry> toCreate = new ArrayList<>();
			List<String> identifier = new ArrayList<>();
			Set<String> missingUsers = new HashSet<>();
			for (RevCommit rev : logs) {
				PersonIdent author = rev.getAuthorIdent();
				String emailAddress = author.getEmailAddress();
				Employee employee = employeeEmailMap.get(emailAddress);
				if (employee != null) {
					if (storage.wasObjectSynced(rev.getId().getName()) != null) {
						// object was already synced.. nothing to do.
						continue;
					}
//					System.out.println("Found user with " + emailAddress);
					toCreate.add(
							new HubEntry(
									employee,
									new Date(rev.getCommitTime() * 1000L),
									null,
									SrcType.SCM,
									"Git Commit ("+projectName+"): " + rev.getShortMessage(),
									urlPrefix == null ? null : urlPrefix + rev.getId().getName()));
					identifier.add(rev.getId().getName());
				} else {
//					System.out.println("No such user: " + emailAddress);
					missingUsers.add(emailAddress);
				}
//				System.out.println("rev: " + rev + " - " + rev.getShortMessage() + " - " + new Date(rev.getCommitTime() * 1000L));
			}
			
			CreateHubEntriesResponse res = auth.createHubEntries(toCreate);
			List<Long> createdList = res.getCreatedList();
			for (int i = 0 ; i < createdList.size() ; i++) {
				Long id = createdList.get(i);
				if (id != null) {
					storage.syncedObject(identifier.get(i), id);
				}
			}
			
			System.out.println("Created " + toCreate.size() + " entries.");
			if (missingUsers.size() > 0) {
				System.out.println("Missing Users: " + missingUsers);
			}
			
		} catch (GitAPIException | IOException | RequestErrorException e) {
			throw new RuntimeException("Error while syncing logs.", e);
		}
	}

}
