package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author: suxun
 * @date: 2020/4/25 22:44
 * @description:
 */
public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> ft = new FutureTask<>(new MyCallable());
        new Thread(ft).start();
        if (!ft.isDone()) {
            System.out.println("task has not finished,plz wait");
        }
        System.out.println("task return : " + ft.get());
    }
}
