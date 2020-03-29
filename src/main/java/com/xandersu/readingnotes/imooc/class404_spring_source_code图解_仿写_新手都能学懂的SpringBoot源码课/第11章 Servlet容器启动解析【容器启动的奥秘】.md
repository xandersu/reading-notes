# 第11章 Servlet容器启动解析

- 启动流程解析
- web容器工厂类加载解析
- web容器个性化配置解析
- 总结

## 容器介绍

Apache Tomcat

- 轻量级web容器



1. 用户请求进来connector在指定端口监听请求
2. connector收到请求后转交给container处理，内部engine（处理引擎）处理
3. engine根据请求路径找到host
4. host交给context处理
5. context将请求转到servlet处理
6. servlet是java的程序，处理结束后返回



## 启动前准备

1. SpringApplication构造方法
2. 复制webApplicationType属性
3. 根据classpath下是否存在特定类来决定
4. SERVLET、REACTIVE、NONE



refresh

createApplicationContext方法

根据webApplicationType属性决定上下文

初始化DEFAULT_SERVLET_WEB_CONTEXT_CLASS



## webServer创建入口

1. refreshContext
2. refresh
3. onRefresh
4. createWebServer

## createWebServer

1. getWebServerFactory
2. factory.getWebServer
3. 设置webServer属性
4. initPropertySources

## servlet启动

1. refresh
2. finishRefresh
3. startWebServer
4. publishEvent



# Web容器工厂类加载解析





