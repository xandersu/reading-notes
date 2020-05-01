# 第11章 Java框架-Spring

IOC

- DL   dependency lookup

- DI    dependency Injection
  - setter set注入
  - Interface 接口注入
  - Annotation 注解注入
  - Constructor 构造器注入



### 依赖注入方式

- setter
  - 实现特定属性的public setter方法，来让容器调用注入所依赖类型的对象
- Interface
  - 实现特定的接口的
- Constructor
  - 实现特定参数的构造方法，来让ioc注入所依赖类型的对象
- Annotation
  - 使用Java注解机制，让ioc容器注入所依赖类型的对象



依赖倒置原则：高层模块不应该依赖底层模块，两者都应该依赖其抽象

一种思想

控制反转  => 第三方容器 =>  控制反转容器 ioc container

方法

依赖注入



### IOC的优势

- 避免在各处使用new来创建类，并且可以做到统一维护
- 创建实例的时候不需要了解其中的细节



[![JXCxPA.png](https://s1.ax1x.com/2020/05/01/JXCxPA.png)](https://imgchr.com/i/JXCxPA)



### Spring ioc支持的功能

- 依赖注入
- 依赖检查
- 自动装配
- 支持集合
- 指定初始化方法和销毁方法
- 支持回调方法（需要实现spring接口）

### Spring ioc容器的核心接口

- BeanFactory
- ApplicationContext



### BeanDefinition

- 主要用于描述Bean的定义



### BeanDefinitionRegistry

- 提供了向IOC容器手工注册BeanDefinition对象的方法



### BeanFactory：Spring框架最核心的接口

- 提供IOC的配置机制
- 包含Bean的各种定义，便于实例化Bean
- 建立Bean之间的依赖关系
- Bean生命周期的控制



[![JXiT0O.png](https://s1.ax1x.com/2020/05/01/JXiT0O.png)](https://imgchr.com/i/JXiT0O)

### org.springframework.beans.factory.ListableBeanFactory

- 定义了访问容器中Bean基本信息的若干方法
- 查看Bean的个数
- 获取某一类型Bean的配置名
- 查看容器中是否包括某一Bean



### org.springframework.beans.factory.HierarchicalBeanFactory

父子级联IOC容器的接口

子容器可以通过接口方法访问父容器

通过HierarchicalBeanFactory spring的ioc容器可以建立父子层级关联的容器体系

spring通过父子容器实现了很多功能：

- 展现层Bean在子容器中

- 业务层bean在父容器中
- 展现层可以使用业务层的Bean
- 业务层的bean看不到展现层的Bean



### org.springframework.beans.factory.config.ConfigurableBeanFactory  重要的接口

增强了IOC容器的可定义性

定义了设置类装载器，属性编辑器，属性初始化后置处理器等方法



### org.springframework.beans.factory.config.AutowireCapableBeanFactory

定义了将容器中的Bean按某种规则，比如按名字匹配、按类型匹配等，按照这些规则对Bean进行装配



### org.springframework.beans.factory.config.SingletonBeanRegistry

允许在运行期间向容器注入Singleton实例bean的方法



### org.springframework.beans.factory.BeanFactory

 在容器中获取Bean

Object getBean(String name)   按名称

<T> T getBean(Class<T> requiredType) 按类型



boolean isSingleton(String name)  判断Bean是否在容器中是单例。默认是单例。

boolean isPrototype(String name) 返回true，当我们使用getBean方法获得Bean，Spring容器会创建一个新的Bean返回给调用者



### BeanFactory和ApplicationContext的比较

org.springframework.context.ApplicationContext是BeanFactory的子接口之一

在spring的体系之中，BeanFactory和ApplicationContext是最为重要的接口设计

在使用spring ioc容器时，大部分是ApplicationContext接口的实现类

BeanFactory发动机，ApplicationContext完整的汽车包括所有组件



- BeanFactory是Spring框架的基础设施，面向Spring本身
- ApplicationContext 是面向使用Spring框架的开发者



### ApplicationContext的功能（继承多个接口）

- BeanFactory：能够管理Bean、装配Bean
- ResourcePatternResolver：能够加载资源文件
- MessageSource：能够实现国际化等功能
- ApplicationEventPublisher：能够注册监听器，实现监听机制 



[![JXkxw8.png](https://s1.ax1x.com/2020/05/01/JXkxw8.png)](https://imgchr.com/i/JXkxw8)





## 11-4 SpringIOC的refresh源码解析

//TODO 



### getBean方法解析

org.springframework.beans.factory.support.AbstractBeanFactory

所有getBean都调用

org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean



### Spring Bean 的作用域

- singleton：Spring的默认作用域，容器里拥有唯一的Bean实例
- prototype：针对每个getBean请求，容器都会创建一个Bean实例（慎用）

Web环境下，新增3个作用域

- request：会为每个Http请求创建一个Bean实例
- session：会为每个session创建一个Bean实例
- globalSession：会为每个全局Http Session创建一个Bean实例，该作用域仅对Portlet有效。
  - 全局Http Session：Portlet提出的概念，Portlet规范定义了全局Session的概念，它被所有构成某个portlet web应用的各种不同的portlet所共享。在global session作用域中定义的bean被限定于全局portlet Session的生命周期范围内。



### Spring Bean的生命周期

#### 创建过程

1. 实例化bean对象 ， 设置Bean属性

2. Aware（注入Bean ID、BeanFactory和AppCtx）

   - 如果实现了各种Aware接口，则会注入Bean对容器基础设施层面的依赖。
   - Aware接口是为了能够感知到自己的一些属性。

   - 容器中的Bean一般不需要了解容器的状态和直接使用容器，但是在某些情况下，需要Bean中直接对IOC容器进行操作的，这时候就需要在Bean中设定对容器的感知。
   - Spring容器提供特定Aware接口完成。

3. BeanPostProcessor(s) .postProcessBeforeInitialization

   - BeanPostProcessor的前置初始化方法
   - 在spring完成实例化之后，对spring容器实例化的bean添加一些自定义的处理逻辑

4. InitializingBean(s) . afterPropertiesSet

   - 如果实现了InitializingBean接口，调用afterPropertiesSet方法
   - 做一些属性被设定后的自定义的事情

5. 定制 Bean init 方法

   - Bean自身的init方法，做一些初始化操作

6. BeanPostProcessor(s) . postProcessAfterInitialization

   - BeanPostProcessor的后置初始化方法
   - Bean实例初始化之后的自定义工作

7. Bean初始化完毕



#### 销毁过程

1. 若实现了DisposableBean接口，则会调用Destroy方法
2. 如果Bean配置了destory-method属性，则会调用其配置的销毁方法



## Spring AOP

关注点分离：不同的问题交给不同的部分去解决

- 面向切面编程AOP正是这种技术的实现
- 通用化功能代码的实现，对应的就是所谓的切面(Aspect)
- 业务功能代码和切面代码分开后，架构将变得高内聚低耦合
- 确保功能的完整性：切面最终需要被合并到业务中（Weave）



### AOP的三种织入方式

1. 编译时织入：需要特殊的Java编译器，如AspectJ
2. 类加载时织入：需要特殊的Java编译器，如AspectJ和AspectWerkz
3. 运行时织入：Spring采用的方式，通过动态代理的方式，实现简单



### AOP的主要名词概念

- Aspect：通用功能的代码实现
- Target：被织入Aspect的对象
- Join Point：可以作为切入点的机会，所有方法都可以作为切入点
- pointcut：Aspect实际被应用在的 Join Point 支持正则
- Advice：类里的方法以及这个方法如何织入到目标方法的方式
- Weaving：Aop的实现过程



### Advice的种类

- 前置通知 Before
- 后置通知 AfterReturing
- 异常通知 AfterThrowing
- 最终通知 After
- 环绕通知 Around



### AOP的实现：JDK Proxy和Cglib

- 有AopProxyFactory根据AdvisedSupport对象的配置来决定
- 默认的策略是如果目标类是接口，则用JDKProxy来实现，否者用Cglib
- JDKProxy的核心：InvocationHandler接口和Proxy类，代理的类必须实现有接口
- Cglib：以继承的方式动态生成目标类的代理
- JDKProxy：通过Java的内部反射机制实现
- Cglib：借助ASM实现
- 反射机制在生成 类的过程中比较高效
- ASM在生成类之后的执行过程中比较搞笑



### 代理模式：接口 + 真实实现类 + 代理类

Spring里的代理模式的实现

- 真实实现类的逻辑包含在了getBean方法里
- getBean方法返回的实际上是Proxy的实例
- Proxy实例是Spring 采用 JDK proxy或者 cglib 动态生成的





- ACID
- 隔离级别
- 事务传播













