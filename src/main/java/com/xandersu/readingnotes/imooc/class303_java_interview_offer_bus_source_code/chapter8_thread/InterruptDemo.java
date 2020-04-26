package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/26 22:31
 * @description:
 */
public class InterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        Runnable interruptTask = () -> {
            int i = 0;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(100);
                    i++;
                    System.out.println(Thread.currentThread().getName() + "----" + Thread.currentThread().getState() + " ) loop " + i);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + "----" + Thread.currentThread().getState() + " ) loop " + i);
            }
        };

        Thread t1 = new Thread(interruptTask, "t1");
        System.out.println(t1.getName() + "----" + t1.getState() + " is new");
        t1.start();
        System.out.println(t1.getName() + "----" + t1.getState() + " is started");

        Thread.sleep(300);
        t1.interrupt();
        System.out.println(t1.getName() + "----" + t1.getState() + " is interrupt");

        Thread.sleep(300);
        System.out.println(t1.getName() + "----" + t1.getState() + " is interrupt now");


    }

}
