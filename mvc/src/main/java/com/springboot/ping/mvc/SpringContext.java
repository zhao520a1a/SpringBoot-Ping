package com.springboot.ping.mvc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring容器上下文工具类
 * 创建者： 刘江平
 * 创建时间：2014年9月15日上午10:17:06
 */
@Component
public class SpringContext implements ApplicationContextAware {
	private static SpringContext sc;
	private static ApplicationContext context;
	
	public synchronized static SpringContext init(){
		if(sc==null){
			sc = new SpringContext();
		}
		return sc;
	}
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	/**
	 * 获取容器中的Bean对象
	 * @param beanid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized  static <T>T getBean(String beanid){
		return (T)context.getBean(beanid);
	}
	
	public synchronized static <T>T getBean(Class<T> beanClass){
		return (T)context.getBean(beanClass);
	}
	
	public synchronized static <T>T getBean(String name,Class<T> beanClass){
		return (T)context.getBean(name,beanClass);
	}
}
