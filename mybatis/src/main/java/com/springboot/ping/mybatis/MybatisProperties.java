package com.springboot.ping.mybatis;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.springboot.ping.common.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * mybatis相关属性
 * Created by 刘江平 on 2016-10-13.
 */
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
public class MybatisProperties {
	public static final String PREFIX = "mybatis";

	/**动态生成实体mapper文件的模板*/
	public static final String MYBATIS_ENTITY_MAPPER_VM = "entityMapper.vm";
	/**查询用的公用的mapper文件*/
	public static final String MYBATIS_COMMON_MAPPER = "common_mapper.xml";
	/**默认的mapperLocations*/
	public static final String DEFAULT_MAPPER_LOCATIONS = "classpath:mapper/*.xml,classpath:mapper/*/*.xml";
	/**
	 * mybatis的configLocation地址
	 */
	private String configLocation;
	/**
	 * mapper文件地址
	 */
	private String[] mapperLocations;
	/**
	 * mybatis相关mapper类和实体类的base包
	 */
	private Set<String> basePackages;
	/**
	 * 加载自定义mapper文件资源
	 * @return
	 */
	public Resource[] resolveMapperLocations() {
		if(mapperLocations==null||mapperLocations.length==0){
			this.mapperLocations = DEFAULT_MAPPER_LOCATIONS.split(",");
		}
		List<Resource> resources = new ArrayList<Resource>();
		for (String mapperLocation : this.mapperLocations) {
			Resource[] mappers;
			try {
				mappers = new PathMatchingResourcePatternResolver().getResources(mapperLocation);
				resources.addAll(Arrays.asList(mappers));
			} catch (IOException e) {

			}
		}

		Resource[] mapperLocations = new Resource[resources.size()];
		mapperLocations = resources.toArray(mapperLocations);
		return mapperLocations;
	}

	/**
	 * 加载mybatis的config文件
	 * @return
	 */
	public Resource resolveConfigLocation() {
		if(StringUtil.isNotEmpty(this.configLocation)){
			return new DefaultResourceLoader().getResource(this.configLocation);
		}
		return null;
	}

	public String getConfigLocation() {
		return configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public String[] getMapperLocations() {
		return mapperLocations;
	}

	public void setMapperLocations(String[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	public Set<String> getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(Set<String> basePackages) {
		this.basePackages = basePackages;
	}

}
