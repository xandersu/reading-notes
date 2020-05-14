package com.xandersu.readingnotes.source;

/**
 * @author su
 * @date 2020/5/1210:12
 * @description
 */
public class TestWait {
    ThreadLocal<String> tl = new ThreadLocal<>();

    public  static void main(String[] args) throws InterruptedException {
        Object o = new Object();
        synchronized (o){
            o.notifyAll();
            o.wait();
        }


    }
}
