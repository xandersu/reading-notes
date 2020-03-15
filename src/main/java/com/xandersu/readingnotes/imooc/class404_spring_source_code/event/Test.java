package com.xandersu.readingnotes.imooc.class404_spring_source_code.event;

/**
 * @author: suxun
 * @date: 2020/3/15 13:24
 * @description:
 */
public class Test {

    public static void main(String[] args) {
        WeatherEventMulticaster weatherEventMulticaster = new WeatherEventMulticaster();
        RainListener rainListener = new RainListener();
        SnowListener snowListener = new SnowListener();
        weatherEventMulticaster.addListener(rainListener);
        weatherEventMulticaster.addListener(snowListener);

        weatherEventMulticaster.multicastEvent(new SnowEvent());
        weatherEventMulticaster.multicastEvent(new RainEvent());

        weatherEventMulticaster.removeListener(rainListener);

        weatherEventMulticaster.multicastEvent(new SnowEvent());
        weatherEventMulticaster.multicastEvent(new RainEvent());

    }
}
