package com.xandersu.readingnotes.imooc.class404_spring_source_code.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: suxun
 * @date: 2020/3/23 21:13
 * @description:
 */
@Component
@Order(2)
public class SecondInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> map = new HashMap<>();
        map.put("key2", "value2");
        MapPropertySource mapPropertySource = new MapPropertySource("secondInitializer", map);
        environment.getPropertySources().addLast(mapPropertySource);
        System.out.println("run SecondInitializer");
    }
}
