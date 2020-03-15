package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml;

import lombok.Data;

/**
 * @author: suxun
 * @date: 2020/3/15 15:58
 * @description:
 */
@Data
public class HelloService {
    private Student student;
    private Animal animal;

    public String hello() {
        return student.toString();
    }

    public String helloAnimal() {
        return animal.getName();
    }
}
