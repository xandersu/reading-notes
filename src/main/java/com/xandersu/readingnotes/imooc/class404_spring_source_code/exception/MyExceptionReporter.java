package com.xandersu.readingnotes.imooc.class404_spring_source_code.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: suxun
 * @date: 2020/3/29 12:33
 * @description:
 */
@Slf4j
public class MyExceptionReporter implements SpringBootExceptionReporter {

    private ConfigurableApplicationContext configurableApplicationContext;

    public MyExceptionReporter(ConfigurableApplicationContext configurableApplicationContext) {
        this.configurableApplicationContext = configurableApplicationContext;
    }

    @Override
    public boolean reportException(Throwable failure) {
        if (failure instanceof UnsatisfiedDependencyException) {
            UnsatisfiedDependencyException exception = (UnsatisfiedDependencyException) failure;
            log.error("no such bean " + exception.getInjectionPoint().getField().getName());
        }
        return false;
    }
}
