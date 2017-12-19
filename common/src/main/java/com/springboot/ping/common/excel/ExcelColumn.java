package com.springboot.ping.common.excel;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;

/**
 * excel表头列储存数据的字段类
 * <br>colindex:列索引,从0开始
 * <br>field:列字段名称  注意此名称与表头列中文名称不是一样，此列名称是指储存数据用的map中key
 * <br>format:该列数据的类型  可选text文本，number数字，amt金额，rate百分比类,默认text文本@see Format
 * <br>align:该列数据的位置   可选center居中,right靠右,left靠左，默认center居中@see Align
 * <br>existZero:此列数据中的0值是否是真实存在的,true代表真实存在，直接展现0,false代表不存在0数据,用"-"代替,默认true
 * <br>创建者： 刘江平
 * 创建时间：2014年11月12日下午2:33:03
 */
public class ExcelColumn {
	/**列索引  从0开始*/
	private int colindex;
	/**列名称  注意此名称与表头列名称不是一样，此列名称是指储存数据用的map中key*/
	private String field;
	/**该列数据的类型  可选text文本，number数字，amt金额，rate百分比类,默认text文本*/
	private Format format = Format.TEXT;
	/**该列数据的位置   可选center居中,right靠右,left靠左，默认center居中*/
	private Align align = Align.center;
	/**此列数据中的0值是否是真实存在的，
	 * true代表真实存在，直接展现0，
	 * false代表不存在0数据,用"-"代替，
	 * 默认true*/
	protected boolean existZero=true;
	public ExcelColumn(){};
	/**
	 * 
	 * @param colindex列索引 ,从0开始
	 * @param field 列名称field,即行数据集map中key
	 * @param format 列数据类型
	 * @param align 列数据位置
	 * @param existZero 此列数据中的0值是否是真实存在的
	 */
	public ExcelColumn(int colindex, String field, Format format, Align align,boolean existZero) {
		this.colindex = colindex;
		this.field = field;
		this.format = format;
		this.align = align;
		this.existZero = existZero;
	}
	/**
	 * 
	 * @param colindex列索引 ,从0开始
	 * @param field 列名称field,即行数据集map中key
	 * @param format 列数据类型
	 * @param align 列数据位置
	 */
	public ExcelColumn(int colindex, String field, Format format, Align align) {
		this.colindex = colindex;
		this.field = field;
		this.format = format;
		this.align = align;
	}
	/**
	 * 
	 * @param colindex列索引 ,从0开始
	 * @param field 列名称field,即行数据集map中key
	 */
	public ExcelColumn(int colindex, String field) {
		this.colindex = colindex;
		this.field = field;
	}
	public int getColindex() {
		return colindex;
	}
	public void setColindex(int colindex) {
		this.colindex = colindex;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public Align getAlign() {
		return align;
	}
	public void setAlign(Align align) {
		this.align = align;
	}
	public boolean isExistZero() {
		return existZero;
	}
	public void setExistZero(boolean existZero) {
		this.existZero = existZero;
	}
}
