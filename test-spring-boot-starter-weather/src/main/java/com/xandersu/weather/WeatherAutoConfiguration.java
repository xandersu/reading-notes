package com.xandersu.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: suxun
 * @date: 2020/3/29 16:26
 * @description:
 */
@Configuration
@EnableConfigurationProperties(WeatherSource.class)
@ConditionalOnProperty(name = "weather.enable", havingValue = "enable")
public class WeatherAutoConfiguration {

    @Autowired
    private WeatherSource weatherSource;

    @Bean
    @ConditionalOnMissingBean(WeatherService.class)
    public WeatherService weatherService() {
        return new WeatherService(weatherSource);
    }
}
