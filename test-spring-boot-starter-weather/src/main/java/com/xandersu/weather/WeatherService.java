package com.xandersu.weather;

/**
 * @author: suxun
 * @date: 2020/3/29 16:23
 * @description:
 */
public class WeatherService {

    private WeatherSource weatherSource;

    public WeatherService(WeatherSource weatherSource) {
        this.weatherSource = weatherSource;
    }

    public String getType() {
        return weatherSource.getType();
    }

    public String getRate() {
        return weatherSource.getRate();
    }
}
