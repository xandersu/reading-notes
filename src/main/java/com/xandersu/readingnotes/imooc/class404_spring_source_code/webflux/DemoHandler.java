package com.xandersu.readingnotes.imooc.class404_spring_source_code.webflux;

import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.DemoRepository;
import com.xandersu.readingnotes.imooc.class404_spring_source_code.mybatis.model.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author: suxun
 * @date: 2020/4/1 20:43
 * @description:
 */
@Component
public class DemoHandler {

    @Autowired
    private DemoRepository demoRepository;

    @Autowired
    private CityRepository cityRepository;

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("hello"), String.class);
    }

    public Mono<ServerResponse> world(ServerRequest request) {
        return ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("world"), String.class);
    }

    public Mono<ServerResponse> times(ServerRequest request) {
        return ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(Flux.interval(Duration.ofSeconds(1))
                        .map(it -> new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(demoRepository.getById(id)), Demo.class);
    }

    public Mono<ServerResponse> listCity(ServerRequest request) {
        return ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(cityRepository.findAll(), City.class);
    }

    public Mono<ServerResponse> saveCity(ServerRequest request) {
        String province = request.pathVariable("province");
        String city = request.pathVariable("city");
        City record = new City();
        record.setProvince(province);
        record.setCity(city);
        Mono<City> mono = Mono.just(record);
        return ok()
                .build(cityRepository.insert(mono).then());
    }


}
