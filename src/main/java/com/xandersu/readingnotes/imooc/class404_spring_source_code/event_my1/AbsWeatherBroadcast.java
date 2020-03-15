package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author: suxun
 * @date: 2020/3/15 13:48
 * @description:
 */
public abstract class AbsWeatherBroadcast implements IBroadcast {

    private List<IWeatherListener> listenerList = Lists.newArrayList();

    @Override
    public void broadcast(WeatherEvent event) {

        onStart();
        listenerList.forEach(item -> item.onWeatherEvent(event));
        onEnd();

    }

    @Override
    public void addListener(IWeatherListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void removeListener(IWeatherListener listener) {
        listenerList.remove(listener);
    }

    protected abstract void onStart();

    protected abstract void onEnd();
}
