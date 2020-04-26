package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/26 21:34
 * @description:
 */
public class WaitSleepDemo_B {

    public static void main(String[] args) throws Exception {
        Object lock = new Object();
        new Thread(() -> {
            System.out.println("thread a 等待获取锁");
            synchronized (lock) {
                System.out.println("thread a 获得锁");
                try {
                    Thread.sleep(20);
                    System.out.println("thread a do wait method");
                    Thread.sleep(1000);
                    System.out.println("thread a is done");
                    System.out.println("thread a 释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(100);
        new Thread(() -> {
            System.out.println("thread b 等待获取锁");
            synchronized (lock) {
                System.out.println("thread b 获得锁");
                try {
                    System.out.println("thread b do wait method 10ms");
                    lock.wait(10);
                    System.out.println("thread b is done");
                    System.out.println("thread b 释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
