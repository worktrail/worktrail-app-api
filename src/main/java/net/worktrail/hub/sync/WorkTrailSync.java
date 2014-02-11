package net.worktrail.hub.sync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.worktrail.hub.sync.git.SyncStorage;
import net.worktrail.hub.sync.response.CreateHubEntriesResponse;
import net.worktrail.hub.sync.response.Employee;
import net.worktrail.hub.sync.response.HubEntry;
import net.worktrail.hub.sync.response.RequestErrorException;

public abstract class WorkTrailSync {
	
	private WorkTrailAppApi auth;
	private SyncStorage storage;
	private Map<String, Employee> employeeEmailMap;
	private Set<String> missingUsers;

	public WorkTrailSync(WorkTrailAppApi auth, SyncStorage storage) {
		this.auth = auth;
		this.storage = storage;
		this.missingUsers = new HashSet<>();
	}

	public abstract List<HubEntry> startHubSync();
	
	public final void prepareHubSync() throws RequestErrorException {
		EmployeeListResponse employeeListResponse = auth.fetchEmployees();
		employeeEmailMap = new HashMap<>();
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
	}

	protected Employee getEmployeeByEmail(String emailAddress) {
		Employee ret = employeeEmailMap.get(emailAddress);
		if (ret == null) {
			missingUsers.add(emailAddress);
		}
		return ret;
	}

	public void finishHubSync(List<HubEntry> toCreate) throws RequestErrorException {
		CreateHubEntriesResponse res = auth.createHubEntries(toCreate);
		List<Long> createdList = res.getCreatedList();
		for (int i = 0 ; i < createdList.size() ; i++) {
			Long id = createdList.get(i);
			if (id != null) {
				storage.syncedObject(toCreate.get(i).getIdentifier(), id);
			}
		}
		
		System.out.println("Created " + toCreate.size() + " entries.");
		if (missingUsers.size() > 0) {
			System.out.println("Missing Users: " + missingUsers);
		}
	}

}
