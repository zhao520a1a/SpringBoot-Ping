package com.springboot.ping.mvc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ping.mvc")
public class PingMvcProperties {
	/**list页面模板*/
	private String listTemplatePath = "easyuiList";
	private String detailTemplatePath = "easyuiDetail";
	private long maxExcelNumber = 60000;
	public String getListTemplatePath() {
		return listTemplatePath;
	}
	public void setListTemplatePath(String listTemplatePath) {
		this.listTemplatePath = listTemplatePath;
	}
	public String getDetailTemplatePath() {
		return detailTemplatePath;
	}
	public void setDetailTemplatePath(String detailTemplatePath) {
		this.detailTemplatePath = detailTemplatePath;
	}
	public long getMaxExcelNumber() {
		return maxExcelNumber;
	}
	public void setMaxExcelNumber(long maxExcelNumber) {
		this.maxExcelNumber = maxExcelNumber;
	}
	
}
