package com.springboot.ping.mybatis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.springboot.ping.mybatis.core.PingSqlSessionFactoryBean;
import com.springboot.ping.mybatis.extend.dao.SqlMapper;
import com.springboot.ping.mybatis.extend.service.MybatisQueryService;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * mybatis加载
 * Created by 刘江平 on 2016-10-14.
 */
@Configuration
@ConditionalOnClass({ SqlSessionFactory.class, PingSqlSessionFactoryBean.class,MybatisProperties.class})
@EnableConfigurationProperties({MybatisProperties.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisConfiguration {
	private static final Logger log = LoggerFactory.getLogger(MybatisConfiguration.class);
	@Autowired
	private BeanFactory beanFactory;
			
    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,MybatisProperties mybatisProperties,DataSourceProperties dataSourceProperties) throws Exception{
    	List<String> pkgs = AutoConfigurationPackages.get(this.beanFactory);
    	Set<String> basePackages = mybatisProperties.getBasePackages();
    	if(basePackages==null){
    		mybatisProperties.setBasePackages(new HashSet<>(pkgs));
    	}else{
    		basePackages.addAll(pkgs);
    	}
    	return new PingSqlSessionFactoryBean(dataSource,mybatisProperties,dataSourceProperties).getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    	return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    @Bean
    @ConditionalOnMissingBean
	public JdbcTemplate jdbcTemplate(DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}
    
    @Bean
    public MybatisQueryService mybatisQueryService(SqlSessionTemplate sqlSessionTemplate){
    	return new MybatisQueryService(sqlSessionTemplate);
    }
    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
        implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

      private BeanFactory beanFactory;

      private ResourceLoader resourceLoader;

      @Override
      public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        log.debug("Searching for mappers interface extends SqlMapper'");

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        try {
          if (this.resourceLoader != null) {
            scanner.setResourceLoader(this.resourceLoader);
          }

          List<String> pkgs = AutoConfigurationPackages.get(this.beanFactory);
          for (String pkg : pkgs) {
            log.debug("Using auto-configuration base package '" + pkg + "'");
          }
          scanner.setAnnotationClass(Mapper.class);
          scanner.setMarkerInterface(SqlMapper.class);
          scanner.registerFilters();
          scanner.doScan(StringUtils.toStringArray(pkgs));
        } catch (IllegalStateException ex) {
          log.debug("Could not determine auto-configuration " + "package, automatic mapper scanning disabled.");
        }
      }

      @Override
      public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
      }

      @Override
      public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
      }
    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components based
     * on the same component-scanning path as Spring Boot itself.
     */
    @Configuration
    @Import({ AutoConfiguredMapperScannerRegistrar.class })
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration {

      @PostConstruct
      public void afterPropertiesSet() {
        log.debug(String.format("No %s found.", MapperFactoryBean.class.getName()));
      }
    }
}
