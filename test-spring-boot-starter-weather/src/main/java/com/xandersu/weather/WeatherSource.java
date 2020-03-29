package com.xandersu.weather;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: suxun
 * @date: 2020/3/29 16:22
 * @description:
 */
@Data
@ToString
@ConfigurationProperties(prefix = "weather")
public class WeatherSource {

    private String type;
    private String rate;

}
