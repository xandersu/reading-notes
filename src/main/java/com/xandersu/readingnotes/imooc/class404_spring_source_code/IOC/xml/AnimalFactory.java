package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml;

/**
 * @author: suxun
 * @date: 2020/3/15 16:05
 * @description:
 */
public class AnimalFactory {

    public static Animal getAnimal(String type) {
        if ("dog".equals(type)) {
            return new Dog();
        } else if ("cat".equals(type)) {
            return new Cat();
        }
        return null;
    }
}
