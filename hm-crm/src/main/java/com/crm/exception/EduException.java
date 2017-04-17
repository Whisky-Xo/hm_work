package com.crm.exception;

import com.crm.api.constant.ResponseConstants;

/**
 * 自定义异常类
 * 
 * @author JingChenglong 2016-09-08 10:06
 *
 */
public class EduException extends Exception {

	private static final long serialVersionUID = 1L;

	private int code;

	public EduException() {
		super(String.valueOf(ResponseConstants.SYSTEMBUSY));
	}

	public EduException(int code) {
		super(String.valueOf(code));
		this.code = code;
	}

	public EduException(String msg) {
		super(msg);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}