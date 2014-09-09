package net.worktrail.appapi.model;

import java.util.Date;

public class WorkEntryImpl implements WorkEntry {
	long id;
	String description;
	Date start;
	Date end;
	long taskId;
	long employeeId;
	long modifyDate;
	
	public WorkEntryImpl() {
	}
	
	

	public WorkEntryImpl(long id, String description, Date start, Date end,
			long taskId, long employeeId, long modifyDate) {
		super();
		this.id = id;
		this.description = description;
		this.start = start;
		this.end = end;
		this.taskId = taskId;
		this.employeeId = employeeId;
		this.modifyDate = modifyDate;
	}



	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Date getStart() {
		return start;
	}

	@Override
	public Date getEnd() {
		return end;
	}

	@Override
	public long getTaskId() {
		return taskId;
	}

	@Override
	public long getEmployeeId() {
		return employeeId;
	}

	@Override
	public long getModifyDate() {
		return modifyDate;
	}

}
