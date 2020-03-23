package com.xandersu.readingnotes.imooc.class404_spring_source_code.properties;

import org.springframework.beans.factory.Aware;

/**
 * @author: suxun
 * @date: 2020/3/23 21:29
 * @description:
 */
public interface MyAware extends Aware {

    void setFlag(Flag f);


}
