package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Animal;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Cat;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 16:37
 * @description:
 */
@Component
public class MyCat implements FactoryBean<Animal> {
    @Override
    public Animal getObject() throws Exception {
        return new Cat();
    }

    @Override
    public Class<?> getObjectType() {
        return Animal.class;
    }
}
