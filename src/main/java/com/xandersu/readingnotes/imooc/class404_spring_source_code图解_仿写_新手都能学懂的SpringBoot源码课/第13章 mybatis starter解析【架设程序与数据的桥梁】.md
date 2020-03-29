# 第13章 mybatis starter解析

- mybatis-starter使用指南
- mybatis-starter原理解析
- 缓存使用
- 总结

## mybatis-starter作用

- 自动检测工程中的DataSource
- 创建并注册SqlSessionFactory实例
- 创建并注册SqlSessionTemplate实例
-  自动扫描mappers

## mybatis-starter引入步骤

1. 引入mybatis-starter、mysql两个jar包
2. 配置数据库连接属性
3. 引入mybatis逆向工程插件及文件
4. 配置mybatis工程属性
5. 注解MapperScan或Mapper以扫描接口类

## mapper类扫描

- @mapperScan("com.xxx.xxx.mapper")
- @Mapper



# mybatis-starter配置类解析

## 自动配置类导入

1. mybatis-spring-boot-starter jar包
2. mybatis-spring-boot-autoconfigure jar包
3. META-INF/spring.factories文件
4. MybatisAutoConfiguration

## 关键类导入

SqlSessionFactory

单个数据库映射关系经编译后的内存镜像

SqlSessionTemplate

## mapper类扫描

MapperScannerRegistrarNotFoundConfiguration

AutoConfiguredMapperScannerRegistrar|Mapperscan

MapperScannerConfigure

扫描mapper接口注册到容器中

## mapper类生成

processBeanDefinitions

beanclass替换成MapperFactoryBean.class

MapperFactoryBean#getObject

MapperProxy对象

## mapper执行

mapperProxy#invoke

mapperMethod#execute

根据数据库操作类型，调用sqlsession操作

返回执行结果



