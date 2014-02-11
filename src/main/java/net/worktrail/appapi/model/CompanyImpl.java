package net.worktrail.appapi.model;

import com.google.common.base.Objects;

public class CompanyImpl implements Company {
	private String name;
	private long id;
	private String slug;
	private long breakTaskId;
	private long orgProjectId;
	private long unassignedProjectId;
	
	
	public CompanyImpl(long id, String name, String slug, long breakTaskId, long orgProjectId, long unassignedProjectId) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.breakTaskId = breakTaskId;
		this.orgProjectId = orgProjectId;
		this.unassignedProjectId = unassignedProjectId;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getId()
	 */
	@Override
	public long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getSlug()
	 */
	@Override
	public String getSlug() {
		return slug;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getBreakTaskId()
	 */
	@Override
	public long getBreakTaskId() {
		return breakTaskId;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getOrgProjectId()
	 */
	@Override
	public long getOrgProjectId() {
		return orgProjectId;
	}
	
	/* (non-Javadoc)
	 * @see net.worktrail.appapi.model.Company#getUnassignedProjectId()
	 */
	@Override
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
