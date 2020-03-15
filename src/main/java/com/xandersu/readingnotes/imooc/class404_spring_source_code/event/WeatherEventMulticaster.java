package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 13:23
 * @description:
 */
@Component
public class WeatherEventMulticaster extends AbsEventMulticaster {

    @Override
    protected void doStart() {
        System.out.println("开始广播");

    }

    @Override
    protected void doEnd() {
        System.out.println("停止广播");

    }
}
