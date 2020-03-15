package com.xandersu.readingnotes.imooc.class404_spring_source_code.listener;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

/**
 * @author: suxun
 * @date: 2020/3/15 14:47
 * @description:
 */
@Order(3)
public class ThirdListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("hello ThirdListener");
    }
}
