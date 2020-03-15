package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

/**
 * @author: suxun
 * @date: 2020/3/15 13:54
 * @description:
 */
public class SnowListener extends AbsWeatherListener {

    @Override
    public void onWeatherEvent(WeatherEvent event) {
        if (event instanceof SnowEvent) {
            super.onWeatherEvent(event);
        }
    }
}
