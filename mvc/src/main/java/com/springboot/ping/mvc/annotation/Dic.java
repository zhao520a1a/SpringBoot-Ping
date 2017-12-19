package com.springboot.ping.mvc.annotation;

/**
 * 字典信息
 * <br>pcode:选填,父节点代码,默认为空
 * <br>创建者： 刘江平
 * 创建时间：2015年7月9日下午4:02:51
 */
public @interface Dic {
	/**
	 * 父节点代码
	 * @return
	 */
	public abstract String group() default "";
}
