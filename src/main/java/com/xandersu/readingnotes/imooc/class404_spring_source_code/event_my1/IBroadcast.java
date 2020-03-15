package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

/**
 * @author: suxun
 * @date: 2020/3/15 13:46
 * @description:
 */
public interface IBroadcast {

    void broadcast(WeatherEvent event);
    void addListener(IWeatherListener listener);
    void removeListener(IWeatherListener listener);
}
