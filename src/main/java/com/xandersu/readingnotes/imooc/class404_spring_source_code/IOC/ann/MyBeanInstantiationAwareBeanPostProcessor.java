//package com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.ann;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
//import org.springframework.stereotype.Component;
//
///**
// * @author: suxun
// * @date: 2020/3/15 19:02
// * @description:
// */
//@Component
//public class MyBeanInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
//    @Override
//    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        if(beanName.equals("worker")){
//            return new Worker();
//        }
//        return null;
//    }
//}
