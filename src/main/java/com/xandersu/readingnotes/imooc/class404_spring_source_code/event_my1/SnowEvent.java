package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;


/**
 * @author: suxun
 * @date: 2020/3/15 13:45
 * @description:
 */
public class SnowEvent implements WeatherEvent {

    @Override
    public String getWeather() {
        return "snow";
    }
}
