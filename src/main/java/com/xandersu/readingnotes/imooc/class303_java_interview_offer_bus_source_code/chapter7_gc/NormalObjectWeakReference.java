package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter7_gc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author: suxun
 * @date: 2020/4/25 16:57
 * @description:
 */
public class NormalObjectWeakReference extends WeakReference<NormalObject> {
    public String getName() {
        return name;
    }

    private String name;


    public NormalObjectWeakReference(NormalObject referent, ReferenceQueue<? super NormalObject> q) {
        super(referent, q);
        this.name = referent.name;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize NormalObjectWeakReference : " + name);
    }
}
