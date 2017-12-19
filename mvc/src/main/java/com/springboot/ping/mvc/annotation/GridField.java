package com.springboot.ping.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;


/**
 * 字段在前端展现或者excel导出时的各类信息
 * <br>name:必填,字段中文名称
 * <br>search:选填,是否要设置成查询条件,默认为不设置成查询条件
 * <br>dicData:选填,此列的字典转义
 * <br>width:选填,前端展现或者excel导出是列的宽度,默认为100px
 * <br>hide:选填,前端展现是否要隐藏此列,默认为false不隐藏
 * <br>detail:选填,明细页面是否要展现此列，默认为true要展现
 * <br>format:选填,格式化数据，默认为文本
 * <br>align:选填,字段的数据显示位置，默认居中显示
 * <br>dicFomart:选填,list页面中字典列的值是否要翻译成字典值,默认为true,会翻译，但必须依赖上面注解dic()配置了,false表示不翻译
 * <br>sortable:选填,list页面是否要支持此字段排序，默认false不支持排序，true代表支持排序
 * <br>创建者： 刘江平
 * 创建时间：2015年7月9日下午2:24:51
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GridField {
	/**
	 * 字段中文名称
	 * @return
	 */
	public abstract String name();
	/**
	 * 是否要设置成查询条件,默认为不设置成查询条件
	 * @return
	 */
	public abstract Search search() default @Search;
	/**
	 * 此列的字典转义
	 * @return
	 */
	public abstract Dic dic() default @Dic;
	/**
	 * 前端展现或者excel导出是列的宽度,默认为100px
	 * @return
	 */
	public abstract int width() default 100;
	/**
	 * 前端展现是否要隐藏此列,默认为false不隐藏
	 * @return
	 */
	public abstract boolean hide() default false;
	/**
	 * 明细页面是否要展现此列，默认为true要展现
	 * @return
	 */
	public abstract boolean detail() default true;
	/**
	 * 格式化数据，默认为文本
	 * @return
	 */
	public abstract Format format() default Format.TEXT;
	/**
	 * 字段的数据显示位置，默认居中显示
	 * @return
	 */
	public abstract Align align() default Align.center;
	/**
	 * list页面中字典列的值是否要翻译成字典值,默认为true,会翻译，但必须依赖上面注解dic()配置了,false表示不翻译
	 * @return
	 */
	public abstract boolean dicFomart() default true;
	
	/**
	 * list页面是否要支持此字段排序，默认false不支持排序，true代表支持排序
	 * @return
	 */
	public abstract boolean sortable() default false;
	
	
}
