package net.worktrail.hub.sync.response;

public class RequestErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public RequestErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}
