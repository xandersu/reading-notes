package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/26 22:16
 * @description:
 */
public class YieldDemo {
    public static void main(String[] args) {

        Runnable yieldTask = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + i);
                if (i == 5) {
                    Thread.yield();
                }
            }
        };
        new Thread(yieldTask, "A").start();
        new Thread(yieldTask, "B").start();
    }
}
