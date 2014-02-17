package net.worktrail.appapi.model;

public enum Privacy {
	PRIVATE("private"),
	COMPANY("company")
	;
	
	private String stringIdentifier;

	private Privacy(String stringIdentifier) {
		this.stringIdentifier = stringIdentifier;
	}
	
	public static final Privacy getPrivacyByStringIdentifier(String identifier) {
		for (Privacy p : Privacy.values()) {
			if (identifier.equals(p.stringIdentifier)) {
				return p;
			}
		}
		return null;
	}
	
	public String getStringIdentifier() {
		return stringIdentifier;
	}
}
