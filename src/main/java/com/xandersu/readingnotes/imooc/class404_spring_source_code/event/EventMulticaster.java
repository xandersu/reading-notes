package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;


/**
 * @author: suxun
 * @date: 2020/3/15 13:11
 * @description: 广播器
 */
public interface EventMulticaster {
    //广播事件
    void multicastEvent(WeatherEvent event);

    void addListener(WeatherListener listener);

    void removeListener(WeatherListener listener);
}
