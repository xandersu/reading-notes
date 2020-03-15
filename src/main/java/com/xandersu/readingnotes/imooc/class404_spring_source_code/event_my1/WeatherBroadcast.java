package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

/**
 * @author: suxun
 * @date: 2020/3/15 13:52
 * @description:
 */
public class WeatherBroadcast extends AbsWeatherBroadcast {
    @Override
    protected void onStart() {
        System.out.println("开始广播");
    }

    @Override
    protected void onEnd() {
        System.out.println("结束广播");
    }
}
