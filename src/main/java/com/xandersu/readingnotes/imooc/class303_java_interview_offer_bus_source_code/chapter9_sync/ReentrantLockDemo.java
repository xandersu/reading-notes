package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter9_sync;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: suxun
 * @date: 2020/4/29 20:48
 * @description:
 */
public class ReentrantLockDemo implements Runnable {
    //    public static ReentrantLock lock = new ReentrantLock(true);
    public static ReentrantLock lock = new ReentrantLock(false);

    @Override
    public void run() {
        while (true) {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " get lock.");
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        new Thread(reentrantLockDemo).start();
        new Thread(reentrantLockDemo).start();
        new Thread(reentrantLockDemo).start();
    }
}
