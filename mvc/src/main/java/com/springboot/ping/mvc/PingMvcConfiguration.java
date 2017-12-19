package com.springboot.ping.mvc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.springboot.ping.mvc.easyui.EasyuiTemplate;
import com.springboot.ping.mvc.meta.PageTemplate;

@Configuration
@EnableConfigurationProperties(PingMvcProperties.class)
public class PingMvcConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public PageTemplate pageTemplate(){
		return new EasyuiTemplate();
	}
}