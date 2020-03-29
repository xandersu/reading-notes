package com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * @author: suxun
 * @date: 2020/3/29 15:54
 * @description:
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({MyConditional.class})
public @interface MyConditionAnnotation {

    String[] value() default {};
}
