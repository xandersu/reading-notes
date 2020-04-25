package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter7_gc;

/**
 * @author: suxun
 * @date: 2020/4/25 16:55
 * @description:
 */
public class NormalObject {
    public String name;

    public NormalObject(String name) {
        this.name = name;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize NormalObject : " + name);
    }
}
