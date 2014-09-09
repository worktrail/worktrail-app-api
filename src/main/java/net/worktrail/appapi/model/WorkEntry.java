package net.worktrail.appapi.model;

import java.util.Date;

public interface WorkEntry {
	
	long getId();
	
	String getDescription();
	
	Date getStart();
	
	Date getEnd();
	
	long getTaskId();
	
	long getEmployeeId();
	
	long getModifyDate();
}
