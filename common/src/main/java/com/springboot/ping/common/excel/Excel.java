package com.springboot.ping.common.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.springboot.ping.common.enums.Align;
import com.springboot.ping.common.enums.Format;


/**
 * excel导出的注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Excel {
	/**
	 * 列名称
	 * @return
	 */
	public abstract String name();
	/**
	 * 列批注
	 * @return
	 */
	public abstract String postil() default "";
	/**
	 * 列数据类型，默认文本
	 * @return
	 */
	public abstract Format format() default Format.TEXT;
	/**
	 * 列数据位置，默认居中
	 * @return
	 */
	public abstract Align align() default Align.center;
	/**
	 * 代码转义中文，数组单元的格式如下
	 * 代码:中文意义
	 * 比如{"0:禁用","1:启用"}
	 * @return
	 */
	public abstract String[] escape() default {};
}
