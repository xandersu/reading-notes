package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter7_gc;


import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author: suxun
 * @date: 2020/4/25 16:15
 * @description:
 */
public class Finalization {
    public static Finalization finalization;


    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalization finalize!!!");
        finalization = this;
    }

    public static void main(String[] args) throws Exception {
        Finalization f = new Finalization();
        System.out.println("first print : " + f);
        f = null;

        System.gc();

        Thread.sleep(1000L);

        System.out.println("second print : " + f);
        System.out.println(f.finalization);

        String str = new String("abc");
        SoftReference<String> stringSoftReference = new SoftReference<>(str);

        WeakReference<String> stringWeakReference = new WeakReference<>(str);

        ReferenceQueue referenceQueue = new ReferenceQueue();
        PhantomReference<String> phantomReference = new PhantomReference<>(str, referenceQueue);
    }


}
