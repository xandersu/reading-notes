package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: suxun
 * @date: 2020/3/15 15:53
 * @description:
 */
@Data
@ToString
public class Student {

    private String name;
    private Integer age;
    private List<String> classList;

    public Student(String name,Integer age){
        this.name = name;
        this.age = age;
    }
}
