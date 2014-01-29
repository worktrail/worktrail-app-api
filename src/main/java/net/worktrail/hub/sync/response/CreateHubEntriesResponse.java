package net.worktrail.hub.sync.response;

import java.util.List;

public class CreateHubEntriesResponse extends WorkTrailResponse {

	private List<Long> createdList;

	public CreateHubEntriesResponse(List<Long> createdList) {
		this.createdList = createdList;
	}
	
	public List<Long> getCreatedList() {
		return createdList;
	}

}
