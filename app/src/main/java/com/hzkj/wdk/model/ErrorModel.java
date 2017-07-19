package com.hzkj.wdk.model;

/**
 * 错误信息
 * 
 * @author berton
 * 
 */
public class ErrorModel {
	/** 错误代码 **/
	private int code;
	/** 错误信息 **/
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
