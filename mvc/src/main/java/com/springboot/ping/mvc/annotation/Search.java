package com.springboot.ping.mvc.annotation;

import com.springboot.ping.mvc.enums.InputType;


/**
 * 查询条件信息
 * <br>search:选填,是否要设置成查询条件,默认为false不设置成查询条件
 * <br>inputType:选填,设置成查询条件后，查询条件的输入框类型，默认为text文本框
 * <br>between:选填,设置成查询条件后,是否设置成一个查询区间的输入,默认false为不按区间查询
 * <br>searchName:选填,此查询条件的显示名称,默认与字段中文名称一样
 * <br>创建者： 刘江平
 * 创建时间：2015年7月17日下午5:16:11
 */
public @interface Search {
	/**
	 * 是否要设置成查询条件,默认为false不设置成查询条件
	 * @return
	 */
	public abstract boolean search() default false;
	/**
	 * 设置成查询条件后，查询条件的输入框类型，默认为text文本框
	 * @return
	 */
	public abstract InputType inputType() default InputType.TEXT;
	/**
	 * 设置成查询条件后,是否设置成一个查询区间的输入,默认false为不按区间查询
	 * @return
	 */
	public abstract boolean between() default false;
	
	/**
	 * 此查询条件的显示名称,默认与字段中文名称一样
	 * @return
	 */
	public abstract String searchName() default ""; 
	
	/**
	 * 下拉款是否单选，默认为true单选,false代表多选,此属性只针对下拉款查询有效
	 * @return
	 */
	public abstract boolean singleSelect() default true;
}
