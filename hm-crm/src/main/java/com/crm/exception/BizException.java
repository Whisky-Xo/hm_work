package com.crm.exception;

public class BizException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1118072755342675962L;

	private int code;

	public BizException(int code, String msg, Throwable throwable) {
		super(msg, throwable);
		this.code = code;
	}

	public BizException(int code, String msg) {
		this(code, msg, null);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
