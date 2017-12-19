package com.springboot.ping.common.util;

import java.util.HashSet;
import java.util.Set;

public class ArrayUtil {
	
	public static Set<String> toSet(String[] array){
		Set<String> set = new HashSet<>();
		for (String obj : array) {
			set.add(obj);
		}
		return set;
	}
}
