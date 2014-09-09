package net.worktrail.appapi;

/**
 * Scopes which define the type of access an app has to user data.
 * 
 * @author herbert
 */
public enum WorkTrailScope {
	READ_TASKS("read-tasks"),
	WRITE_TASKS("write-tasks"),
	READ_EMPLOYEES("read-employees"),
	SYNC_HUB_DATA("sync-hub-data"),
	READ_WORKENTRIES("read-workentries"),
	WRITE_WORKENTRIES("write-workentries")
	;
	
	private String stringIdentifier;

	private WorkTrailScope(String stringIdentifier) {
		this.stringIdentifier = stringIdentifier;
	}
	
	public String getStringIdentifier() {
		return stringIdentifier;
	}
}
