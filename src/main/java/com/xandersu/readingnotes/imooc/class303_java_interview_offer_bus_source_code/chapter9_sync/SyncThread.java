package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter9_sync;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: suxun
 * @date: 2020/4/28 21:07
 * @description:
 */
public class SyncThread implements Runnable {

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        if (name.startsWith("A")) {
            async();
        } else if (name.startsWith("B")) {
            syncObjBlock1();
        } else if (name.startsWith("C")) {
            syncObjMethod1();
        }
    }

    private synchronized void syncObjMethod1() {
        System.out.println(Thread.currentThread().getName() + "_syncObjMethod1: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println(Thread.currentThread().getName() + "_syncObjMethod1_Start: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "_syncObjMethod1_End: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private void syncObjBlock1() {
        System.out.println(Thread.currentThread().getName() + "_syncObjBlock1: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + "_syncObjBlock1_Start: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "_syncObjBlock1_End: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
    }

    private void async() {
        System.out.println(Thread.currentThread().getName() + "_Async_Start: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "_Async_End: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    private void syncClassBlock1() {
        System.out.println(Thread.currentThread().getName() + " syncClassBlock1: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        synchronized (SyncThread.class) {
            System.out.println(Thread.currentThread().getName() + "_syncClassBlock1_Start: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "_syncClassBlock1_End: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
    }

    private synchronized static void syncClassBlock2() {
        System.out.println(Thread.currentThread().getName() + " syncClassBlock2: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println(Thread.currentThread().getName() + "_syncClassBlock2_Start: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "_syncClassBlock2_End: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    public static void main(String[] args) {
        SyncThread syncThread = new SyncThread();
        Thread a_thread1 = new Thread(syncThread, "A_thread1");
        Thread a_thread2 = new Thread(syncThread, "A_thread2");
        Thread b_thread1 = new Thread(syncThread, "B_thread1");
        Thread b_thread2 = new Thread(syncThread, "B_thread2");
        Thread c_thread1 = new Thread(syncThread, "C_thread1");
        Thread c_thread2 = new Thread(syncThread, "C_thread2");

        a_thread1.start();
        a_thread2.start();
        b_thread1.start();
        b_thread2.start();
        c_thread1.start();
        c_thread2.start();
    }
}
