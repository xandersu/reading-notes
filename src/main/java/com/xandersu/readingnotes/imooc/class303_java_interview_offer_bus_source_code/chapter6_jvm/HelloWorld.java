package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

/**
 * @author: suxun
 * @date: 2020/4/25 13:43
 * @description:
 */
public class HelloWorld {
    private String name;
    public void sayHello(){
        System.out.println("Hello "+name );
    }

    public void setName(String name){
        this.name = name;
    }

    public static void main(String[] args) {
        int a = 1;
        HelloWorld hw = new HelloWorld();
        hw.setName("test");
        hw.sayHello();
    }
}
