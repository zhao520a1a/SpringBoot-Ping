package com.springboot.ping.generic.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * 响应到前端的对象
 * @author 刘江平 2017年1月7日下午3:32:26
 *
 */
@ApiModel("操作成功与否的对象")
public class Response {
	@ApiModelProperty("操作是否成功")
	private boolean success;
	@ApiModelProperty("成功或者失败提示信息")
	private String msg;
	@ApiModelProperty("其他数据")
	private Map<String,Object> data;
	public Response(){}
	public Response(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}
	public Response(boolean success, Map<String, Object> data) {
		this.success = success;
		this.data = data;
	}

	public Response(boolean success, String msg, Map<String, Object> data) {
		this.success = success;
		this.msg = msg;
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
