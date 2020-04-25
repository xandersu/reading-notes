package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author: suxun
 * @date: 2020/4/25 22:51
 * @description: 线程池获取线程的返回值
 */
public class ThreadPoolDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> submit = executorService.submit(new MyCallable());
        if (!submit.isDone()) {
            System.out.println("task has not finished,plz wait");
        }
        System.out.println("task return : " + submit.get());
        executorService.shutdown();
    }
}
