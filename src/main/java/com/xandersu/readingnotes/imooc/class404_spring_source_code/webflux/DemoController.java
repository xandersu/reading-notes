package com.xandersu.readingnotes.imooc.class404_spring_source_code.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author: suxun
 * @date: 2020/4/1 20:37
 * @description:
 */
@RestController
public class DemoController {

    @GetMapping("/demo1")
    public Mono<String> demo(){
        return Mono.just("demo");
    }
}
