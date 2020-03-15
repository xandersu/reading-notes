package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Animal;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Dog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: suxun
 * @date: 2020/3/15 16:31
 * @description:
 */
@Configuration
public class BeanConfig {

    @Bean("dog3")
    public Animal getDog3(){
        return new Dog();
    }
}
