package com.xandersu.readingnotes.imooc.class404_spring_source_code.event_my1;

/**
 * @author: suxun
 * @date: 2020/3/15 13:53
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        SnowListener snowListener = new SnowListener();
        RainListener rainListener = new RainListener();

        WeatherBroadcast weatherBroadcast = new WeatherBroadcast();
        weatherBroadcast.addListener(snowListener);
        weatherBroadcast.addListener(rainListener);

        weatherBroadcast.broadcast(new SnowEvent());
        weatherBroadcast.broadcast(new RainEvent());

        weatherBroadcast.removeListener(snowListener);

        weatherBroadcast.broadcast(new SnowEvent());
        weatherBroadcast.broadcast(new RainEvent());



    }
}
