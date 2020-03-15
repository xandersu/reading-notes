package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;


/**
 * @author: suxun
 * @date: 2020/3/15 13:44
 * @description:
 */
@FunctionalInterface
public interface IWeatherListener {

    void onWeatherEvent(WeatherEvent event);
}
