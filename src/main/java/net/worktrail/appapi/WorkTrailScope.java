package net.worktrail.appapi;

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
