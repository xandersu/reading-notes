package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

/**
 * @author: suxun
 * @date: 2020/3/15 13:08
 * @description:
 */
public class SnowEvent extends WeatherEvent {
    @Override
    public String getWeather() {
        return "snow";
    }
}
