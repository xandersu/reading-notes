package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

import lombok.Data;
import lombok.ToString;

/**
 * @author: suxun
 * @date: 2020/4/24 21:42
 * @description:
 */
@Data
@ToString
public class Robot {

    private String name;

    public void sayHi(String hello) {
        System.out.println(hello + " " + name);
    }

    private String throwHello(String tag) {
        return "hello " + tag;
    }
}
