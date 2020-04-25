package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter7_gc;

import com.google.common.collect.Lists;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author: suxun
 * @date: 2020/4/25 16:58
 * @description:
 */
public class ReferenceQueueTest {
    private static ReferenceQueue<NormalObject> rq = new ReferenceQueue<>();

    private static void checkQueue() {
        Reference<NormalObject> ref = null;
        while ((ref = (Reference<NormalObject>) rq.poll()) != null) {
            if (ref != null) {
                System.out.println("in queue" + ((NormalObjectWeakReference) ref).getName());
                System.out.println("reference obj " + ref.get());
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        List<WeakReference<NormalObject>> weakReferenceList = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            weakReferenceList.add(new NormalObjectWeakReference(new NormalObject("Weak " + i), rq));
            System.out.println("create weak : " + weakReferenceList.get(i));
        }
        System.out.println("first time");
        checkQueue();
        System.gc();
        Thread.sleep(1000L);
        System.out.println("second time");
        checkQueue();
    }
}
