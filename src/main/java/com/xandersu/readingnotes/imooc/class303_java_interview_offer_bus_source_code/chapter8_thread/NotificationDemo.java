package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/26 22:01
 * @description:
 */
public class NotificationDemo {
    private volatile boolean go = false;

    public static void main(String[] args) throws InterruptedException {
        final NotificationDemo test = new NotificationDemo();

        Runnable waitTask = () -> {
            try {
                test.shouldGo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finished Execution");
        };
        Runnable notifyTask = () -> {
            try {
                test.go();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finished Execution");
        };

        new Thread(waitTask, "WT1").start();
        new Thread(waitTask, "WT2").start();
        new Thread(waitTask, "WT3").start();
        Thread.sleep(200);
        new Thread(notifyTask, "NT1").start();
    }

    private synchronized void go() throws InterruptedException {
        while (!go) {
            System.out.println(Thread.currentThread() + " is going to wait on this object");
            this.wait();
            System.out.println(Thread.currentThread() + " is woken up");
        }
        go = false;
    }


    private synchronized void shouldGo() {
        while (go) {
            System.out.println(Thread.currentThread() + " is  going to notify all or one thread waiting on thread ");
            go = true;
//            notify();
            notifyAll();
        }
    }
}
