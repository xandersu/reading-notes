package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 14:21
 * @description:
 */
@Component
public class WeatherRunListener {

    @Autowired
    private WeatherEventMulticaster weatherEventMulticaster;

    public void snow(){
        weatherEventMulticaster.multicastEvent(new SnowEvent());
    }

    public void rain(){
        weatherEventMulticaster.multicastEvent(new RainEvent());
    }

}
