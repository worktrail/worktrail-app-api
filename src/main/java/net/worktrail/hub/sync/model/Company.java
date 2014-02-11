package net.worktrail.hub.sync.model;

import com.google.common.base.Objects;

public class Company {
	private String name;
	private long id;
	private String slug;
	private long breakTaskId;
	private long orgProjectId;
	private long unassignedProjectId;
	
	
	public Company(long id, String name, String slug, long breakTaskId, long orgProjectId, long unassignedProjectId) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.breakTaskId = breakTaskId;
		this.orgProjectId = orgProjectId;
		this.unassignedProjectId = unassignedProjectId;
	}
	
	public String getName() {
		return name;
	}
	
	public long getId() {
		return id;
	}
	
	public String getSlug() {
		return slug;
	}
	
	public long getBreakTaskId() {
		return breakTaskId;
	}
	
	public long getOrgProjectId() {
		return orgProjectId;
	}
	
	public long getUnassignedProjectId() {
		return unassignedProjectId;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("name", name)
				.toString();
	}
}
