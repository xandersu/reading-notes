# 第12章 SpringBoot starter解析

- conditional注解解析
- 动手搭建starter
- starter原理解析
- 总结



# conditional注解介绍

- 含义：基于条件的注解
- 作用：是否满足某一个特定条件来决定是否创建某个特定的bean
- 意义：SpringBoot实现自动配置的关键基础能力

## 常见的conditional注解

- @ConditionalOnBean
- @ConditionalOnClass
- @ConditionalOnWebApplication
- @ConditionalOnNotWebApplication
- @ConditionalOnMissingBean
- @ConditionalOnMissingClass
- @ConditionalOnProperty
- @ConditionalOnJava

## 自定义conditional注解实现

- 实现一个自定义注解并且引入conditional注解
- 实现conditional接口重写matches方法，符合条件返回true
- 自定义注解引入Condition接口实现类



# Starter介绍

- 简介：可插拔插件
- 与jar包区别：starter能实现自动配置
- 作用：大幅提高开发效率

## 常用starter

|                  名称                  | 描述                                                         |
| :------------------------------------: | ------------------------------------------------------------ |
|     spring-boot-starter-thymeleaf      |                                                              |
|        spring-boot-starter-mall        |                                                              |
|     spring-boot-starter-data-redis     |                                                              |
|        spring-boot-starter-web         | 构建web，包含restful风格框架springMVC和默认的嵌入式容器tomcat |
|      spring-boot-starter-activemq      |                                                              |
| spring-boot-starter-data-elasticsearch |                                                              |
|        spring-boot-starter-aop         |                                                              |
|      spring-boot-starter-security      |                                                              |
|      spring-boot-starter-data-jpa      |                                                              |
|          spring-boot-starter           |                                                              |
|     spring-boot-starter-freemarker     |                                                              |
|       spring-boot-starter-batch        |                                                              |
|     spring-boot-starter-data-solar     |                                                              |
|    spring-boot-starter-data-mongodb    |                                                              |



## 新建starter步骤

1. 新建SpringBoot项目
2. 引入spring-boot-autoconfigure
3. 编写属性源及自动配置类
4. 在spring.factories中添加自动配置类实现
5. maven打包

## 使用starter步骤

1. pom.xml引用starter
2. 配置文件中配置属性
3. 类中引用服务
4. 调用服务能力



## starter自动配置类导入

- 启动类上@SpringBootApplication
- 引入AutoConfigurationImportSelector
- ConfigurationClassParser中处理
- 获取spring.factories中EnableAutoConfiguration实现

## starter自动配置类过滤

1. @ConditionalOnProperty
2. OnPropertyCondition
3. getMatchOutCome
4. 遍历注解属性集判断environment中是否含有并值一致
5. 返回对比结果



