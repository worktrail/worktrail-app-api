package net.worktrail.appapi.response;

public enum SrcType {
	ISSUES("issues"),
	ERP("erp"),
	SCM("scm"),
	PHONE("phone"),
	GEO("geo"),
	OTHER("other");
	
	private String stringIdentifier;

	private SrcType(String stringIdentifier) {
		this.stringIdentifier = stringIdentifier;
	}
	
	public String getStringIdentifier() {
		return stringIdentifier;
	}
}
