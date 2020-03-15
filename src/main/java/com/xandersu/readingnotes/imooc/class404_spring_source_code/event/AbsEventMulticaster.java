package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: suxun
 * @date: 2020/3/15 13:13
 * @description:
 */
public abstract class AbsEventMulticaster implements EventMulticaster {

    @Autowired
    private List<WeatherListener> listenerList ;


    @Override
    public void multicastEvent(WeatherEvent event) {
        doStart();
        for (WeatherListener weatherListener : listenerList) {
            weatherListener.onWeatherEvent(event);
        }
        doEnd();
    }

    @Override
    public void addListener(WeatherListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void removeListener(WeatherListener listener) {
        listenerList.remove(listener);
    }

    protected abstract void doStart();

    protected abstract void doEnd();

}
