package com.springboot.ping.generic;

import com.springboot.ping.generic.service.GenericService;
import com.springboot.ping.generic.service.SpringContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 刘江平 on 2017-01-17.
 */
@Configuration
public class GenericControllerConfiguration {

    @Bean
    public GenericService genericService(){
        return new GenericService();
    }

    @Bean
    public SpringContext springContext(){
        return new SpringContext();
    }
}
