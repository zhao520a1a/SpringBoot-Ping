package com.springboot.ping.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * datagrid的属性信息，比如是否单选，标题，toolbar操作按钮等
 * <br>singleSelect:选填,列表是否单选，默认为true单选
 * <br>title:必填,列表的标题
 * <br>oper:选填,列表的toolbar操作按钮信息,默认为空
 * <br>remoteSort:选填,是否基于后台查询排序，默认false，代表当页排序，true代表后台查询排序
 * <br>multiSort:选填,是否支持多字段排序，默认false，单字段排序，true代表多字段排序
 * <br>allSort:选填,是否所有字段都支持排序，默认false，可通过GridField注解属性sortable配置某个字段是否支持排序，如果为true，则代表所有字段都支持排序，GridField注解属性sortable配置将失效
 * <br>创建者： 刘江平
 * 创建时间：2015年7月14日下午6:13:44
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Grid {
	/**
	 * 列表是否单选，默认为true单选
	 * @return
	 */
	public abstract boolean singleSelect() default true;
	/**
	 * 列表的标题
	 * @return
	 */
	public abstract String title();
	/**
	 * 列表的toolbar操作按钮信息
	 * @return
	 */
	public abstract GridToolBar[] oper() default {};
	
	/**
	 * 是否基于后台查询排序，默认false，代表当页排序，true代表后台查询排序
	 * @return
	 */
	public abstract boolean remoteSort() default false;
	
	/**
	 * 是否支持多字段排序，默认false，单字段排序，true代表多字段排序
	 * @return
	 */
	public abstract boolean multiSort() default false;
	/**
	 * 是否所有字段都支持排序，默认false，可通过GridField注解属性sortable配置某个字段是否支持排序，如果为true，则代表所有字段都支持排序，GridField注解属性sortable配置将失效
	 * @return
	 */
	public abstract boolean allSort() default false;
}
