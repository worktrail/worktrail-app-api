package net.worktrail.appapi.response;

import java.util.Collection;

import net.worktrail.appapi.model.WorkEntry;

public class WorkEntryListResponse extends WorkTrailResponse {
	private long numPages;
	private long page;
	private Collection<WorkEntry> workEntryList;

	public WorkEntryListResponse(long numPages, long page, Collection<WorkEntry> workEntryList) {
		this.numPages = numPages;
		this.page = page;
		this.workEntryList = workEntryList;
	}
	
	public long getNumPages() {
		return numPages;
	}
	
	public long getPage() {
		return page;
	}
	
	public Collection<WorkEntry> getWorkEntryList() {
		return workEntryList;
	}
}
