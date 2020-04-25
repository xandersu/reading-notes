package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter7_gc;

import lombok.Data;
import lombok.ToString;

/**
 * @author: suxun
 * @date: 2020/4/25 14:40
 * @description:
 */
public class RefrenceCounterProblem {

    public static void main(String[] args) {
        MyObject o1 = new MyObject();
        MyObject o2 = new MyObject();

        o1.childNode = o2;
        o2.childNode = o1;
    }

    @Data
    @ToString
    public static class MyObject {
        public MyObject childNode;
    }
}
