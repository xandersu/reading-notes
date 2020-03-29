package com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional;

import org.springframework.stereotype.Component;

/**
 * @author: suxun
 * @date: 2020/3/29 15:41
 * @description:
 */
@Component
//@ConditionalOnProperty("com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional")
@MyConditionAnnotation({"com.c1", "com.c2"})
public class A {
}
