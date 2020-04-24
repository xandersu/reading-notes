package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: suxun
 * @date: 2020/4/24 21:43
 * @description:
 */
public class ReflectSample {

    public static void main(String[] args) throws Exception {
        Class<?> rc = Class.forName("com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm.Robot");

        Robot robot = (Robot) rc.newInstance();
        System.out.println("class name = "+rc.getName());
        robot.sayHi("sample");
        Method throwHello = rc.getDeclaredMethod("throwHello", String.class);
        throwHello.setAccessible(true);
        Object bob = throwHello.invoke(robot, "Bob");
        System.out.println("get hello result is : "+bob);

        Method sayHi = rc.getMethod("sayHi", String.class);
        sayHi.invoke(robot,"welcome1");

        Field name = rc.getDeclaredField("name");
        name.setAccessible(true);
        name.set(robot,"hahahaha");

        sayHi.invoke(robot,"welcome2");
    }
}
