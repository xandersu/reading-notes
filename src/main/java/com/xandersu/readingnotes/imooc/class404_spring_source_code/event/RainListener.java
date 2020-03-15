package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/15 13:09
 * @description:
 */
@Component

public class RainListener implements WeatherListener {
    @Override
    public void onWeatherEvent(WeatherEvent event) {
        if(event instanceof RainEvent){
            System.out.println("hello "+ event.getWeather());
        }
    }
}
