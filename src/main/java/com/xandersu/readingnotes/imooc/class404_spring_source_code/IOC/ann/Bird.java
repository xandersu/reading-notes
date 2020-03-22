package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Animal;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 16:39
 * @description:
 */
@Component
public class Bird extends Animal {
    @Override
    public String getName() {
        return "Bird";
    }
}
