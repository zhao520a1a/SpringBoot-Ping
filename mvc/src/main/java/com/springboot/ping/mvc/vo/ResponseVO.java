package com.springboot.ping.mvc.vo;

import java.util.HashMap;
import java.util.Map;

public class ResponseVO {
	private boolean success=true;
	private Map<String,Object> data;
	/**
	 * 成功操作的返回信息
	 * @param msg 返回的提示信息,key是msg
	 */
	public ResponseVO(String msg) {
		data = new HashMap<>();
		data.put("msg", msg);
	}
	/**
	 * @param success 操作是否成功
	 * @param msg 返回的提示信息,key是msg
	 */
	public ResponseVO(boolean success,String msg) {
		this.success = success;
		data = new HashMap<>();
		data.put("msg", msg);
	}
	/**
	 * 
	 * @param success 操作是否成功
	 * @param data 返回的数据信息
	 */
	public ResponseVO(boolean success, Map<String, Object> data) {
		this.success = success;
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
}
