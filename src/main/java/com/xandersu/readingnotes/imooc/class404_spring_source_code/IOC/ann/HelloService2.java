package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Animal;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 15:58
 * @description:
 */
@Data
@Component
public class HelloService2 {
    @Autowired
    @Qualifier(value = "bird")
    private Animal animal;

    public String hello() {
        return animal.getName();
    }

}
