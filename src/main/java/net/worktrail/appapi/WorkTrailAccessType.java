package net.worktrail.appapi;

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
