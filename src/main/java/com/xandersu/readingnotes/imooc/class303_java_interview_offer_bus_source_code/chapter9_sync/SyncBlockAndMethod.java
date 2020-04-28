package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter9_sync;

/**
 * @author: suxun
 * @date: 2020/4/28 22:10
 * @description:
 */
public class SyncBlockAndMethod {

    public void syncTask1(){
        synchronized (this){
            System.out.println("hello 1");
        }
    }

    public synchronized void syncTask2(){
        System.out.println("Hello 2");
    }
}
