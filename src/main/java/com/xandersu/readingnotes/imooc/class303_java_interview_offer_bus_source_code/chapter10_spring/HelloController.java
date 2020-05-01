package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter10_spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: suxun
 * @date: 2020/5/1 16:52
 * @description:
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "helloWorld";
    }

}
