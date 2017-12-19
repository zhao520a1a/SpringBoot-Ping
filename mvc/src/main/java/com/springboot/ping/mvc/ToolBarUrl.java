package com.springboot.ping.mvc;

/**
 * 常量类
 * <br>创建者： 刘江平
 * 创建时间：2015年7月15日下午3:35:30
 */
public class ToolBarUrl {
	/**通用明细按钮的url*/
	public static final String URL_DETAIL = "detail";
	/**通用单选删除按钮的url*/
	public static final String URL_DELETE = "delete";
	/**通用多选批量删除按钮的url*/
	public static final String URL_DELETE_BATCH = "deleteBatch";
	/**通用excel导出按钮的url，会对导出的数据根据注解格式化和转义字典数据*/
	public static final String URL_EXPORT_EXCEL = "excel";
	/**通用excel导出按钮的url，不会对导出的数据根据注解格式化和转义字典数据*/
	public static final String URL_EXPORT_EXCEL_NOFORMAT = "excelNoFormat";
}
