package com.xandersu.readingnotes.imooc.class404_spring_source_code.properties;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/23 20:57
 * @description:
 */
@Component
public class ResultCommandLineRunner implements CommandLineRunner, EnvironmentAware, MyAware {

    private Environment environment;
    private Flag flag;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("flag.isCanOperate()= " + flag.isCanOperate());
        System.out.println(environment.getProperty("test.test"));
        System.out.println(environment.getProperty("test.random"));
        System.out.println(environment.getProperty("test.path"));
        System.out.println(environment.getProperty("test.vm.name"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setFlag(Flag f) {
        flag = f;
    }
}
