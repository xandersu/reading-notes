package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/25 22:08
 * @description: 一个程序是一个可执行的文件，而一个进程则是一个执行中程序的实例
 */
public class CurrentThreadDemo {

    public static void main(String[] args) {
        System.out.println("current thread name = " + Thread.currentThread().getName());
    }
}
