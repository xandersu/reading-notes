package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

/**
 * @author: suxun
 * @date: 2020/4/24 22:20
 * @description:
 */
public class ClassLoaderCheck {

    public static void main(String[] args) throws Exception {
        MyClassLoader cl = new MyClassLoader("/Users/suxun/IdeaProjects/my-github/reading-notes/src/main/resources/reflect/", "myClassLoader");
        Class<?> clazz = cl.loadClass("Wali");
        System.out.println(clazz.getClassLoader());
        clazz.newInstance();

        System.out.println(cl.getParent());
        System.out.println(cl.getParent().getParent());
        System.out.println(cl.getParent().getParent().getParent());
    }
}
