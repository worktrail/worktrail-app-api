package net.worktrail.appapi.response;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class HubEntry {
	private Employee employee;
	private Date time;
	private Date endTime;
	private SrcType srcType;
	private String summary;
	private String link;
	private String identifier;

	/**
	 * 
	 * @param identifier the identifier in the local system, NOT in WorkTrail.
	 * @param employee
	 * @param time
	 * @param endTime
	 * @param srcType
	 * @param summary
	 * @param link
	 */
	public HubEntry(String identifier, Employee employee, Date time, Date endTime, SrcType srcType, String summary, String link) {
		this.identifier = identifier;
		this.employee = employee;
		this.time = time;
		this.endTime = endTime;
		this.srcType = srcType;
		this.summary = summary;
		this.link = link;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Employee getEmployee() {
		return employee;
	}

	public Date getTime() {
		return time;
	}

	public Date getEndTime() {
		return endTime;
	}

	public SrcType getSrcType() {
		return srcType;
	}

	public String getSummary() {
		return summary;
	}

	public String getLink() {
		return link;
	}

	public JSONObject toJSONObject() {
		try {
			JSONObject ret = new JSONObject();
			if (employee != null) {
				ret.put("employee_id", employee.getEmployeeId());
			}
			ret.put("time", time.getTime() / 1000);
			if (endTime != null) {
				ret.put("endtime", endTime.getTime() / 1000);
			}
			ret.put("srctype", srcType.getStringIdentifier());
			ret.put("summary", summary);
			ret.put("link", link);
			return ret;
		} catch (JSONException e) {
			throw new RuntimeException("Error while converting hub entry to json.", e);
		}
		
	}

	
}
