package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/25 22:22
 * @description:
 */
public class MyThread extends Thread {
    private String name;

    public MyThread(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Thread start : " + this.name + " ,i = " + i);
        }
    }

    public static void main(String[] args) {
        MyThread thread666 = new MyThread("thread666");
        MyThread thread999 = new MyThread("thread999");
        MyThread thread888 = new MyThread("thread888");
        MyThread thread777 = new MyThread("thread777");

        thread666.start();
        thread999.start();
        thread888.start();
        thread777.start();
    }
}
