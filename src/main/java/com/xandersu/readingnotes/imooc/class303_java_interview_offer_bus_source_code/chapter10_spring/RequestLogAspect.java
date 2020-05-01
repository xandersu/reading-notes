package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter10_spring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: suxun
 * @date: 2020/5/1 16:53
 * @description:
 */

//@Aspect
//@Component
@Slf4j
public class RequestLogAspect {

    @Pointcut("execution(public * com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter10_spring.*(..)))")
    public void webLog() {

    }

    @Before("webLog()")
    public void webLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        log.info("URL = {}", request.getRequestURL().toString());
        log.info("IP = {}", request.getRemoteAddr());
    }

    @AfterReturning(value = "webLog()", returning = "ret")
    public void afterReturning(Object ret) {
        log.info("return = {}", ret);
    }

}
