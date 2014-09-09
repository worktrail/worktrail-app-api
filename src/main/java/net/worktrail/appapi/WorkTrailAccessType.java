package net.worktrail.appapi;

/**
 * The access type of an auth token - it can either access employee level data or
 * company level data (has access to all employees).
 * 
 * @author herbert
 */
public enum WorkTrailAccessType {
	COMPANY("company"),
	EMPLOYEE("employee"),
	;
	
	private String stringIdentifier;

	private WorkTrailAccessType(String stringIdentifier) {
		this.stringIdentifier = stringIdentifier;
	}
	
	public String getStringIdentifier() {
		return stringIdentifier;
	}
}
