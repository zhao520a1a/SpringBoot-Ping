package com.springboot.ping.mybatis.core;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;

import com.springboot.ping.common.util.ResourceUtil;
import com.springboot.ping.mybatis.MybatisProperties;
import com.springboot.ping.mybatis.meta.BaseMeta;
import com.springboot.ping.mybatis.meta.BeanInfo;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;


/**
 * 继承自SqlSessionFactoryBean类，实现Mapper基本操作模块化功能
 * 通过模板动态加载所有entity对应的mapper文件
 */
public class PingSqlSessionFactoryBean extends SqlSessionFactoryBean {
	private static final Logger logger = LoggerFactory.getLogger(PingSqlSessionFactoryBean.class);
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	public PingSqlSessionFactoryBean(DataSource dataSource, MybatisProperties mybatisProperties, DataSourceProperties dataSourceProperties){
		BaseMeta.init(mybatisProperties,dataSourceProperties);
		this.setDataSource(dataSource);
		Resource configResource = mybatisProperties.resolveConfigLocation();
		if(configResource!=null){
			this.setConfigLocation(configResource);
		}
		Resource[] mapperResources = mybatisProperties.resolveMapperLocations();
		this.setMapperLocations(mapperResources);
		this.setPlugins(new Interceptor[]{new PaginationInterceptor()});
	}

	/**
	 * 在此方法中加入了动态mapper 文件插入
	 * @param mapperLocations  已静态写好的mapper资源文件列表
	 */
	@Override
	public void setMapperLocations(Resource[] mapperLocations) {
		//1.生成mapper资源文件对象
		List<MapperResource> resources = createEntitiesMapperResources();
		
		//2.将原始source和生成的source整合
		try {
			super.setMapperLocations(mergeMapperResources(mapperLocations,
					resources));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 将自定义的mapper文件和common_mapper.xml加到动态生成的mapper文件中。返回合并后的resource集合。
	 * 
	 * @param sources
	 *            资源文件列表
	 * @param dynamicResources
	 *            动态生成的资源文件
	 * @return 添加后的资源
	 * @throws Exception
	 */
	private Resource[] mergeMapperResources(Resource[] sources,
											List<MapperResource> dynamicResources) throws Exception {
		List<Resource> mergedResources = new ArrayList<Resource>();
		//存储自定义的mapper文件资源
		Map<String, MapperResource> mapperMap = new HashMap<String, MapperResource>();
		// 把自定义的mapper文件按namespace进行拆分。目前namespace相同的mapper文件只能自定义到一个文件中去。
		for (Resource resource : sources) {
			String mapperText = ResourceUtil.readResourceAsString(resource);
			MapperResource mr = new MapperResource(mapperText, resource);
			mapperMap.put(mr.getNamespace(), mr);
		}
		// 将生成的动态mapper文件和自定义的mapper文件进行合并。
		for (MapperResource dynamicMapper : dynamicResources) {
			MapperResource mapper = mapperMap.get(dynamicMapper.getNamespace());
			if (mapper != null) {
				String finalMapper = dynamicMapper.getMapperText().replace(
						dynamicMapper.getSqlNodes(),
						dynamicMapper.getSqlNodes() + mapper.getSqlNodes());

				mapperMap.remove(mapper.getNamespace());
				mergedResources
						.add(new InputStreamResource(new ByteArrayInputStream(
								finalMapper.getBytes("UTF-8")), mapper
								.getNamespace()));
			} else { // 如果同名的nampespace下没有自定义的mapper文件，则加入动态生成的mapper文件
				mergedResources.add(new InputStreamResource(
						new ByteArrayInputStream(dynamicMapper.getMapperText()
								.getBytes("UTF-8")), dynamicMapper
								.getNamespace()));
			}
		}
		// 加入所有用户自定义的和动态生成合并后剩下的用户自定义的mapper文件。
		for (MapperResource mr : mapperMap.values()) {
			mergedResources.add(mr.getResource());
		}
		//加入common_mapper.xml
		mergedResources.add(this.resourceLoader.getResource(MybatisProperties.MYBATIS_COMMON_MAPPER));
		return mergedResources.toArray(new Resource[] {});

	}

	/**
	 * 将与数据库对应的所有entity生成mapper对应的基本文件
	 * @return
	 */
	private List<MapperResource> createEntitiesMapperResources() {
		List<MapperResource> mappers = new ArrayList<MapperResource>();
		Map<Class<?>,BeanInfo> beanInfos = BaseMeta.getBeanMaps();
		for (Iterator<Map.Entry<Class<?>,BeanInfo>> it = beanInfos.entrySet().iterator(); it.hasNext();) {
			Entry<Class<?>, BeanInfo> entry = it.next();
			mappers.add(MapperCRUDFactory.getInstance().buildMapper(entry.getValue()));
		}
		logger.info("依据模板生成的mapper资源文件个数["+mappers.size()+"]");
		return mappers;
	}

}
