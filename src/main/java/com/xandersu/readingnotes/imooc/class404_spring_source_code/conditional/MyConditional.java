package com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author: suxun
 * @date: 2020/3/29 15:54
 * @description:
 */
public class MyConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes("com.xandersu.readingnotes.imooc.class404_spring_source_code.conditional.MyConditionAnnotation");

        String[] value = (String[]) annotationAttributes.get("value");

        for (String property : value) {
            if (StringUtils.isEmpty(context.getEnvironment().getProperty(property))) {
                return false;
            }
        }
        return true;
    }
}
