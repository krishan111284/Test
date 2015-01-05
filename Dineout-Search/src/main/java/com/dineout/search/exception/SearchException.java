package com.dineout.search.exception;

public class SearchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4004908842245921935L;
	
	String errorCode;

	public SearchException() {
		super();
	}

	public SearchException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public SearchException(String message, Throwable cause, String errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public SearchException(Throwable cause, String errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
