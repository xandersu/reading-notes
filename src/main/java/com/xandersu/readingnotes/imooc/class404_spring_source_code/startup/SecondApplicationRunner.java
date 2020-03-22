package com.xandersu.readingnotes.imooc.class404_spring_source_code.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/22 14:28
 * @description:
 */
@Order(2)
@Component
public class SecondApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("\u001b[32m >>> startup SecondApplicationRunner<<<]");
    }
}
