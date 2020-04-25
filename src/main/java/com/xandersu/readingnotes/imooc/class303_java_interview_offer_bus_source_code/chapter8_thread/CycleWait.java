package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter8_thread;

/**
 * @author: suxun
 * @date: 2020/4/25 22:35
 * @description: 主线程等待法。让主线程循环等待直到目标子线程返回值为止。
 */
public class CycleWait implements Runnable {

    private String value;

    @Override
    public void run() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = "we have data now";
    }

    public static void main(String[] args) throws InterruptedException {
        CycleWait target = new CycleWait();
        Thread thread = new Thread(target);
        thread.start();
        //主线程等待法。让主线程循环等待直到目标子线程返回值为止。
//        while(target.value == null){
//            Thread.sleep(1000L);
//        }
        //使用Thread类的join阻塞当前线程以等待子线程处理完毕。
        thread.join();
        System.out.println("value: " + target.value);

    }
}
