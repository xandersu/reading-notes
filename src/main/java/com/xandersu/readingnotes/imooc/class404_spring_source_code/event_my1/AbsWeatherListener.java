package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

/**
 * @author: suxun
 * @date: 2020/3/15 13:58
 * @description:
 */
public abstract class AbsWeatherListener implements IWeatherListener {
    @Override
    public void onWeatherEvent(WeatherEvent event) {
        System.out.println(event.getWeather());
    }
}
