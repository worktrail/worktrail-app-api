package net.worktrail.appapi.response;

import org.json.JSONObject;

public class RequestErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	private JSONObject causeResponseObj;
	
	public RequestErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	public RequestErrorException(String message, Throwable cause, JSONObject causeResponseObj) {
		super(message, cause);
		this.causeResponseObj = causeResponseObj;
	}
	
	
	public JSONObject getResponseObject() {
		return causeResponseObj;
	}

}
