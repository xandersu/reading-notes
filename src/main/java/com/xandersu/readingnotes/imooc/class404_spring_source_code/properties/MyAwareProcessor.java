package com.xandersu.readingnotes.imooc.class404_spring_source_code.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/23 21:30
 * @description:
 */
@Component
public class MyAwareProcessor implements BeanPostProcessor {

    private final ConfigurableApplicationContext configurableApplicationContext;

    public MyAwareProcessor(ConfigurableApplicationContext configurableApplicationContext) {
        this.configurableApplicationContext = configurableApplicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Aware) {
            if (bean instanceof MyAware) {
                ((MyAware) bean).setFlag((Flag) configurableApplicationContext.getBean("flag"));
            }
        }
        return bean;
    }
}
