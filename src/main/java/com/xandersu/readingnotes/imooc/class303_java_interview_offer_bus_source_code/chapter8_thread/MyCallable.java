package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

import java.util.concurrent.Callable;

/**
 * @author: suxun
 * @date: 2020/4/25 22:43
 * @description: 通过Callable接口实现：通过FutureTask Or 线程池获取
 */
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        String value = "test";
        System.out.println("ready to work");
        Thread.sleep(2000L);
        System.out.println("task done");
        return value;
    }


}
