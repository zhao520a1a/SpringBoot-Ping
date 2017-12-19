package com.springboot.ping.common.util;

import java.util.Collection;

/**
 * 集合工具类
 * <br>创建者： 刘江平
 * 创建时间：2016年3月17日下午6:20:44
 */
public class CollectionUtil {
	/**
	 * 判断集合是否包含元素
	 * @param datas
	 * @return 返回true代表元素为空，false代表元素不为空
	 */
	public static boolean isEmpty(Collection<?> datas){
		if(datas==null){
			return true;
		}
		if(datas.size()==0){
			return true;
		}
		return false;
	}
}
