# 第14章 webflux解析

- webflux介绍
- webflux指南
- webflux实践
- webflux解析

- 总结

## webflux建议

- 如果当前跑的好没必要换，如果要换，就换全套技术栈
- 如果个人感兴趣，可在小项目里使用或使用WebClient尝试
- 大团队慎重考虑和评估



## webflux技术依赖

1. Reactive Streams：反应式编程标准和规范
2. Reactor：基于Reactive Streams的反应式编程框架
3. webflux：以Reactor为基础实现Web领域的反应式编程框架



## Reactive Streams

一套基于jvm面向流式类库的标准和规范

1. 具有出力无限数量数据的能力
2. 按序处理数据
3. 异步非阻塞的传递数据
4. 必须实现非阻塞的背压



## api规范组件

- publisher:数据发布者
- subscriber:数据订阅者
- subscription:订阅信号
- processor：处理器（包含了发布者和订阅者的混合体）

## Reactor简介

- Reactor框架是spring公司开发的
- 符合Reactive Streams规范
- 侧重于server端的响应式编程
- 两个模块：reactor-core和reactor-ipc



## Reactor的publisher

- 实现：Flux Mono
- Flux：代表一个包含0...N..个元素的响应式序列
- Mono：代表一个包含0/1个元素的结果



## Reactor操作符

map操作符

flatMap操作符

filter



## Reactor操作符和Java8 stream区别

- 形似神不似
- reactor：push模式，服务端推送数据给客户端
- stream：pull模式，客户端主动向服务端请求数据



## reactor创建线程方式

- Schedulers.immediate():当期线程
- Schedulers.single():可重用单线程
- Schedulers.elastic:弹性线程池
- Schedulers.parallel():固定大小线程池
- Schedulers.fromExecutorService():自定义线程池（官方不推荐）



## 线程模型

源数据流 => map => publishOn => filter => publishOn => flatMap => subscribeOn



## 线程切换总结

- publishOn：将上游信号传给下游，同时改变后续的操作符的执行的所在线程，知道下一个publishOn出现在这个链上
- subscribeOn：作用于向上的订阅链，无论处于操作链的什么位置，他都会影响到源头的线程执行环境，但不会影响后续的publishOn



# webflux实践

- 兼容mvc
- spring webflux写法
- 连接关系型数据库的案例
- 连接非关系型数据库的案例



## webflux连接mongo

use test

db.city.insert({province:'jiangxi',city:'nanchang'})

db.city.insert({province:'zhejiang',city:'hangzhou'})

db.city.insert({province:'hunan',city:'changsha'})

db.city.find()



## 兼容springMVC写法

- 使用springMVC注解
- ServletReq/Resp 换成 ServerReq/Resp
- 返回Mono对象

## Webflux函数式写法

1. 定义DemoHandler
2. 写ServerRequest消费方法
3. 定义routerConfig
4. 书写路由映射

## 连接非关系型数据库的案例

spring-boot-starter-data-mongo-reactive

application.properties 文件定义mongo相关属性

定义MongoDB集合对应对象

继承ReactiveMongoRepository



## webflux解析

### DispatchHandler准备

1. setApplicationContextAware
2. initStrategies
3. 获取容器中HandlerMapping及子接口实现
4. 获取容器中HandlerAdapter及子接口实现
5. 获取容器中HandlerResultHandler及子接口实现



### RouterFunctionMapping实例化

1. afterPropertiesSet
2. initRouterFunctions
3. routerFunctions获得系统中所有RouterFunction
4. 通过RouterFunction::andOther将对象返回
5. 返回SameComposedRouterFunction对象

### DispatcherHandler#handle

1. 构建基于handlerMappings集合flux对象
2. 通过concatMap将其转换成handler对象
3. 取出第一个handler对象，若为空则抛错
4. 不为空，调用invokeHandler过的response
5. 调用handleResult对结果进行处理

### HandlerMapping#getHandler

1. 调用子类getHandlerInternal实现
2. 获得Handler对象
3. 跨域处理
4. 返回Mono<Object>对象

### DispatchHandler#invokehandler

1. 遍历handlerAdapters集合
2. 依次调用集合元素supports方法
3. 获取具体实现类调用handle方法
4. 进入具体url对应处理类处理请求
5. 返回Mono<HandlerResult>对象

### DispacherHandler#handleResult

1. 遍历resultHandlers集合
2. 依次调用集合元素supports方法
3. 获取具体实现类调用handleResult方法
4. 将请求结果信息写入ServerWebExchange对象







 