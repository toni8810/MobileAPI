package mobile.rest.api.domain;

public class ResponseObject {
	private boolean success;
	private String message;
	private int statusCode;
	
	public ResponseObject(boolean success, String message, int statusCode) {
		this.success = success;
		this.message = message;
		this.statusCode = statusCode;
	}
	
	public ResponseObject() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
	
	
}
