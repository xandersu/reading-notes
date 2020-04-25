package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/25 22:12
 * @description:
 */
public class ThreadTest {
    private static void attack() {
        System.out.println("fight");
        System.out.println("current thread is : " + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                attack();
            }
        };
        System.out.println("current main thread is : " + Thread.currentThread().getName());
        thread.run();
        thread.start();
    }
}
