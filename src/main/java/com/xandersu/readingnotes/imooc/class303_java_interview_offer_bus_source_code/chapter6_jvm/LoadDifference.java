package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

/**
 * @author: suxun
 * @date: 2020/4/25 11:14
 * @description:
 */
public class LoadDifference {
    public static void main(String[] args) throws Exception {
        ClassLoader cl = Robot.class.getClassLoader();
        Class<?> clazzLoadClass = cl.loadClass("com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm.Robot");

//        Class clazz = Class.forName("com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm.Robot");


    }
}
