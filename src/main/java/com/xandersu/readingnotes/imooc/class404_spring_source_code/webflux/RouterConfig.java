package com.xandersu.readingnotes.imooc.class404_spring_source_code.webflux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @author: suxun
 * @date: 2020/4/1 20:45
 * @description:
 */
@Configuration
public class RouterConfig {

    @Autowired
    private DemoHandler demoHandler;

    @Bean
    public RouterFunction<ServerResponse> demoRouter() {
        return route(GET("/hello"), demoHandler::hello)
                .andRoute(GET("/world"), demoHandler::world)
                .andRoute(GET("/times"), demoHandler::times)
                .andRoute(GET("/getById/{id}"), demoHandler::getById)
//                .andRoute(GET("/listCity"), demoHandler::listCity)
//                .andRoute(GET("/saveCity/{province}/{city}"), demoHandler::saveCity)

                ;
    }
}
