package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/25 22:27
 * @description:
 */
public class MyRunnable implements Runnable {
    private String name;

    public MyRunnable(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Thread start : " + this.name + " ,i = " + i);
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyRunnable("Thread1"));
        Thread thread2 = new Thread(new MyRunnable("Thread2"));
        Thread thread3 = new Thread(new MyRunnable("Thread3"));
        Thread thread4 = new Thread(new MyRunnable("Thread4"));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
