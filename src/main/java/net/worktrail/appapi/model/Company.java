package net.worktrail.appapi.model;

public interface Company {

	String getName();

	long getId();

	String getSlug();

	long getBreakTaskId();

	long getOrgProjectId();

	long getUnassignedProjectId();

}