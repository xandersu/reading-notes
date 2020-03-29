package com.xandersu.readingnotes.imooc.class404_spring_source_code.startup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/22 14:23
 * @description:
 */
@Order(2)
@Component
public class SecondCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> startup second runner<<<");
    }
}
