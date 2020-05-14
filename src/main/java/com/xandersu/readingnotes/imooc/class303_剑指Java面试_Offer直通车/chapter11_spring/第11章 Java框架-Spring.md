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



## Spring Bean生命周期比较复杂，可以分为创建和销毁两个过程。

首先，创建Bean会经过一系列的步骤，主要包括：
实例化Bean对象。
设置Bean属性。
如果我们通过各种Aware接口声明了依赖关系，则会注入Bean对容器基础设施层面的依赖。具体包括BeanNameAware、BeanFactoryAware和ApplicationContextAware，分别会注入Bean ID、Bean Factory或者ApplicationContext。
调用BeanPostProcessor的前置初始化方法postProcessBeforeInitialization。
如果实现了InitializingBean接口，则会调用afterPropertiesSet方法。
调用Bean自身定义的init方法。
调用BeanPostProcessor的后置初始化方法postProcessAfterInitialization。
创建过程完毕。

第二，Spring Bean的销毁过程会依次调用DisposableBean的destroy方法和Bean自身定制的destroy方法。

### Spring的基础机制。

首先，我们先来看看Spring的基础机制，至少你需要理解下面两个基本方面。
控制反转（Inversion of Control），或者也叫依赖注入（Dependency Injection），广泛应用于Spring框架之中，可以有效地改善了模块之间的紧耦合问题。
从Bean创建过程可以看到，它的依赖关系都是由容器负责注入，具体实现方式包括带参数的构造函数、setter方法或者AutoWired方式实现。
AOP，我们已经在前面接触过这种切面编程机制，Spring框架中的事务、安全、日志等功能都依赖于AOP技术，下面我会进一步介绍。

### Spring框架的涵盖范围。

我前面谈到的Spring，其实是狭义的Spring Framework，其内部包含了依赖注入、事件机制等核心模块，也包括事务、O/R Mapping等功能组成的数据访问模块，以及Spring
MVC等Web框架和其他基础组件。
广义上的Spring已经成为了一个庞大的生态系统，例如：
Spring Boot，通过整合通用实践，更加自动、智能的依赖管理等，Spring Boot提供了各种典型应用领域的快速开发基础，所以它是以应用为中心的一个框架集合。
Spring Cloud，可以看作是在Spring Boot基础上发展出的更加高层次的框架，它提供了构建分布式系统的通用模式，包含服务发现和服务注册、分布式配置管理、负载均衡、分
布式诊断等各种子系统，可以简化微服务系统的构建。
当然，还有针对特定领域的Spring Security、Spring Data等。
上面的介绍比较笼统，针对这么多内容，如果将目标定得太过宽泛，可能就迷失在Spring生态之中，我建议还是深入你当前使用的模块，如Spring MVC。并且，从整体上把握主要
前沿框架（如Spring Cloud）的应用范围和内部设计，至少要了解主要组件和具体用途，毕竟如何构建微服务等，已经逐渐成为Java应用开发面试的热点之一。

### Spring AOP自身设计的一些细节，前面第24讲偏重于底层实现原理，这样还不够全面，毕竟不管是动态代理还是字节码操纵，都还只是基础，更需要Spring层面对切面编程的支持。

先问一下自己，我们为什么需要切面编程呢？
切面编程落实到软件工程其实是为了更好地模块化，而不仅仅是为了减少重复代码。通过AOP等机制，我们可以把横跨多个不同模块的代码抽离出来，让模块本身变得更加内聚，进
而业务开发者可以更加专注于业务逻辑本身。从迭代能力上来看，我们可以通过切面的方式进行修改或者新增功能，这种能力不管是在问题诊断还是产品能力扩展中，都非常有用。
在之前的分析中，我们已经分析了AOP Proxy的实现原理，简单回顾一下，它底层是基于JDK动态代理或者cglib字节码操纵等技术，运行时动态生成被调用类型的子类等，并实例化
代理对象，实际的方法调用会被代理给相应的代理对象。但是，这并没有解释具体在AOP设计层面，什么是切面，如何定义切入点和切面行为呢？
Spring AOP引入了其他几个关键概念：
Aspect，通常叫作方面，它是跨不同Java类层面的横切性逻辑。在实现形式上，既可以是XML文件中配置的普通类，也可以在类代码中用“@Aspect”注解去声明。在运行
时，Spring框架会创建类似Advisor来指代它，其内部会包括切入的时机（Pointcut）和切入的动作（Advice）。
Join Point，它是Aspect可以切入的特定点，在Spring里面只有方法可以作为Join Point。
Advice，它定义了切面中能够采取的动作。如果你去看Spring源码，就会发现Advice、Join Point并没有定义在Spring自己的命名空间里，这是因为他们是源自AOP联盟，可以
看作是Java工程师在AOP层面沟通的通用规范。
Java核心类库中同样存在类似代码，例如Java 9中引入的Flow API就是Reactive Stream规范的最小子集，通过这种方式，可以保证不同产品直接的无缝沟通，促进了良好实践的
推广。
具体的Spring Advice结构请参考下面的示意图。

其中，BeforeAdvice和AfterAdvice包括它们的子接口是最简单的实现。而Interceptor则是所谓的拦截器，用于拦截住方法（也包括构造器）调用事件，进而采取相应动作，所
以Interceptor是覆盖住整个方法调用过程的Advice。通常将拦截器类型的Advice叫作Around，在代码中可以使用“@Around”来标记，或者在配置中使用“<aop:around>”。
如果从时序上来看，则可以参考下图，理解具体发生的时机。

Pointcut，它负责具体定义Aspect被应用在哪些Join Point，可以通过指定具体的类名和方法名来实现，或者也可以使用正则表达式来定义条件。
你可以参看下面的示意图，来进一步理解上面这些抽象在逻辑上的意义。

Join Point仅仅是可利用的机会。
极客时间
Pointcut是解决了切面编程中的Where问题，让程序可以知道哪些机会点可以应用某个切面动作。
而Advice则是明确了切面编程中的What，也就是做什么；同时通过指定Before、After或者Around，定义了When，也就是什么时候做。
在准备面试时，如果在实践中使用过AOP是最好的，否则你可以选择一个典型的AOP实例，理解具体的实现语法细节，因为在面试考察中也许会问到这些技术细节。
如果你有兴趣深入内部，最好可以结合Bean生命周期，理解Spring如何解析AOP相关的注解或者配置项，何时何地使用到动态代理等机制。为了避免被庞杂的源码弄晕，我建议你
可以从比较精简的测试用例作为一个切入点，如CglibProxyTests。
另外，Spring框架本身功能点非常多，AOP并不是它所支持的唯一切面技术，它只能利用动态代理进行运行时编织，而不能进行编译期的静态编织或者类加载期编织。例如，
在Java平台上，我们可以使用Java Agent技术，在类加载过程中对字节码进行操纵，比如修改或者替换方法实现等。在Spring体系中，如何做到类似功能呢？你可以使
用AspectJ，它具有更加全面的能力，当然使用也更加复杂。


# 【死磕 Spring】—— IoC 之加载 Bean：创建 Bean（五）之循环依赖处理


﻿这篇分析 #doCreateBean(...) 方法的第三个过程：循环依赖处理。其实，循环依赖并不仅仅只是在 #doCreateBean(...) 方法中处理，而是在整个加载 bean 的过程中都有涉及。所以，本文内容并不仅仅只局限于 #doCreateBean(...) 方法，而是从整个 Bean 的加载过程进行分析。

1. 什么是循环依赖
循环依赖，其实就是循环引用，就是两个或者两个以上的 bean 互相引用对方，最终形成一个闭环，如 A 依赖 B，B 依赖 C，C 依赖 A。如下图所示：

循环依赖
循环依赖

循环依赖，其实就是一个死循环的过程，在初始化 A 的时候发现引用了 B，这时就会去初始化 B，然后又发现 B 引用 C，跑去初始化 C，初始化 C 的时候发现引用了 A，则又会去初始化 A，依次循环永不退出，除非有终结条件。

Spring 循环依赖的场景有两种：

构造器的循环依赖。
field 属性的循环依赖。
对于构造器的循环依赖，Spring 是无法解决的，只能抛出 BeanCurrentlyInCreationException 异常表示循环依赖，所以下面我们分析的都是基于 field 属性的循环依赖。

在博客 《【【死磕 Spring】—— IoC 之开启 Bean 的加载》 中提到，Spring 只解决 scope 为 singleton 的循环依赖。对于scope 为 prototype 的 bean ，Spring 无法解决，直接抛出 BeanCurrentlyInCreationException 异常。

为什么 Spring 不处理 prototype bean 呢？其实如果理解 Spring 是如何解决 singleton bean 的循环依赖就明白了。这里先卖一个关子，我们先来关注 Spring 是如何解决 singleton bean 的循环依赖的。

2. 解决循环依赖
2.1 getSingleton
我们先从加载 bean 最初始的方法 AbstractBeanFactory 的 #doGetBean(final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly) 方法开始。

在 #doGetBean(...) 方法中，首先会根据 beanName 从单例 bean 缓存中获取，如果不为空则直接返回。代码如下：

// AbstractBeanFactory.java

Object sharedInstance = getSingleton(beanName);
调用 #getSingleton(String beanName, boolean allowEarlyReference) 方法，从单例缓存中获取。代码如下：

// DefaultSingletonBeanRegistry.java

@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 从单例缓冲中加载 bean
    Object singletonObject = this.singletonObjects.get(beanName);
    // 缓存中的 bean 为空，且当前 bean 正在创建
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        // 加锁
        synchronized (this.singletonObjects) {
            // 从 earlySingletonObjects 获取
            singletonObject = this.earlySingletonObjects.get(beanName);
            // earlySingletonObjects 中没有，且允许提前创建
            if (singletonObject == null && allowEarlyReference) {
                // 从 singletonFactories 中获取对应的 ObjectFactory
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    // 获得 bean
                    singletonObject = singletonFactory.getObject();
                    // 添加 bean 到 earlySingletonObjects 中
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    // 从 singletonFactories 中移除对应的 ObjectFactory
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return singletonObject;
}
这个方法主要是从三个缓存中获取，分别是：singletonObjects、earlySingletonObjects、singletonFactories 。三者定义如下：

// DefaultSingletonBeanRegistry.java

/**
 * Cache of singleton objects: bean name to bean instance.
 *
 * 存放的是单例 bean 的映射。
 *
 * 对应关系为 bean name --> bean instance
 */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

/**
 * Cache of singleton factories: bean name to ObjectFactory.
 *
 * 存放的是【早期】的单例 bean 的映射。
 *
 * 对应关系也是 bean name --> bean instance。
 *
 * 它与 {@link #singletonObjects} 的区别区别在，于 earlySingletonObjects 中存放的 bean 不一定是完整的。
 *
 * 从 {@link #getSingleton(String)} 方法中，中我们可以了解，bean 在创建过程中就已经加入到 earlySingletonObjects 中了，
 * 所以当在 bean 的创建过程中就可以通过 getBean() 方法获取。
 * 这个 Map 也是解决【循环依赖】的关键所在。
 **/
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

/**
 * Cache of early singleton objects: bean name to bean instance.
 *
 * 存放的是 ObjectFactory 的映射，可以理解为创建单例 bean 的 factory 。
 *
 * 对应关系是 bean name --> ObjectFactory
 */
private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
singletonObjects ：单例对象的 Cache 。
singletonFactories ： 单例对象工厂的 Cache 。
earlySingletonObjects ：提前曝光的单例对象的 Cache 。
它们三，就是 Spring 解决 singleton bean 的关键因素所在，我称他们为三级缓存：

第一级为 singletonObjects
第二级为 earlySingletonObjects
第三级为 singletonFactories
这里，我们已经通过 #getSingleton(String beanName, boolean allowEarlyReference) 方法，看到他们是如何配合的。详细分析该方法之前，提下其中的 #isSingletonCurrentlyInCreation(String beanName) 方法和 allowEarlyReference 变量：

#isSingletonCurrentlyInCreation(String beanName) 方法：判断当前 singleton bean 是否处于创建中。bean 处于创建中，也就是说 bean 在初始化但是没有完成初始化，有一个这样的过程其实和 Spring 解决 bean 循环依赖的理念相辅相成。因为 Spring 解决 singleton bean 的核心就在于提前曝光 bean 。
allowEarlyReference 变量：从字面意思上面理解就是允许提前拿到引用。其实真正的意思是，是否允许从 singletonFactories 缓存中通过 #getObject() 方法，拿到对象。为什么会有这样一个字段呢？原因就在于 singletonFactories 才是 Spring 解决 singleton bean 的诀窍所在，这个我们后续分析。
#getSingleton(String beanName, boolean allowEarlyReference) 方法，整个过程如下：

首先，从一级缓存 singletonObjects 获取。
如果，没有且当前指定的 beanName 正在创建，就再从二级缓存 earlySingletonObjects 中获取。
如果，还是没有获取到且允许 singletonFactories 通过 #getObject() 获取，则从三级缓存 singletonFactories 获取。如果获取到，则通过其 #getObject() 方法，获取对象，并将其加入到二级缓存 earlySingletonObjects 中，并从三级缓存 singletonFactories 删除。代码如下：

// DefaultSingletonBeanRegistry.java

singletonObject = singletonFactory.getObject();
this.earlySingletonObjects.put(beanName, singletonObject);
this.singletonFactories.remove(beanName);
这样，就从三级缓存升级到二级缓存了。
😈 所以，二级缓存存在的意义，就是缓存三级缓存中的 ObjectFactory 的 #getObject() 方法的执行结果，提早曝光的单例 Bean 对象。
2.2 addSingletonFactory
上面是从缓存中获取，但是缓存中的数据从哪里添加进来的呢？一直往下跟会发现在 AbstractAutowireCapableBeanFactory 的 #doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) 方法中，有这么一段代码：

// AbstractAutowireCapableBeanFactory.java

boolean earlySingletonExposure = (mbd.isSingleton() // 单例模式
        && this.allowCircularReferences // 运行循环依赖
        && isSingletonCurrentlyInCreation(beanName)); // 当前单例 bean 是否正在被创建
if (earlySingletonExposure) {
    if (logger.isTraceEnabled()) {
        logger.trace("Eagerly caching bean '" + beanName +
                "' to allow for resolving potential circular references");
    }
    // 提前将创建的 bean 实例加入到 singletonFactories 中
    // 这里是为了后期避免循环依赖
    addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
}
当一个 Bean 满足三个条件时，则调用 #addSingletonFactory(...) 方法，将它添加到缓存中。三个条件如下：
单例
运行提前暴露 bean
当前 bean 正在创建中
#addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) 方法，代码如下：

// DefaultSingletonBeanRegistry.java

protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
	Assert.notNull(singletonFactory, "Singleton factory must not be null");
	synchronized (this.singletonObjects) {
		if (!this.singletonObjects.containsKey(beanName)) {
			this.singletonFactories.put(beanName, singletonFactory);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}
}
从这段代码我们可以看出，singletonFactories 这个三级缓存才是解决 Spring Bean 循环依赖的诀窍所在。同时这段代码发生在 #createBeanInstance(...) 方法之后，也就是说这个 bean 其实已经被创建出来了，但是它还不是很完美（没有进行属性填充和初始化），但是对于其他依赖它的对象而言已经足够了（可以根据对象引用定位到堆中对象），能够被认出来了。所以 Spring 在这个时候，选择将该对象提前曝光出来让大家认识认识。
2.3 addSingleton
介绍到这里我们发现三级缓存 singletonFactories 和 二级缓存 earlySingletonObjects 中的值都有出处了，那一级缓存在哪里设置的呢？在类 DefaultSingletonBeanRegistry 中，可以发现这个 #addSingleton(String beanName, Object singletonObject) 方法，代码如下：

// DefaultSingletonBeanRegistry.java

protected void addSingleton(String beanName, Object singletonObject) {
	synchronized (this.singletonObjects) {
		this.singletonObjects.put(beanName, singletonObject);
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);
	}
}
添加至一级缓存，同时从二级、三级缓存中删除。
这个方法在我们创建 bean 的链路中有哪个地方引用呢？其实在前面博客 LZ 已经提到过了，在 #doGetBean(...) 方法中，处理不同 scope 时，如果是 singleton，则调用 #getSingleton(...) 方法，如下图所示：

getSingleton
getSingleton

前面几篇博客已经分析了 #createBean(...) 方法，这里就不再阐述了，我们关注 #getSingleton(String beanName, ObjectFactory<?> singletonFactory) 方法，代码如下：

// AbstractBeanFactory.java

public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(beanName, "Bean name must not be null");
    synchronized (this.singletonObjects) {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            //....
            try {
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            //.....
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        return singletonObject;
    }
}
😈 注意，此处的 #getSingleton(String beanName, ObjectFactory<?> singletonFactory) 方法，在 AbstractBeanFactory 类中实现，和 「2.1 getSingleton」 不同。
3. 小结
至此，Spring 关于 singleton bean 循环依赖已经分析完毕了。所以我们基本上可以确定 Spring 解决循环依赖的方案了：

Spring 在创建 bean 的时候并不是等它完全完成，而是在创建过程中将创建中的 bean 的 ObjectFactory 提前曝光（即加入到 singletonFactories 缓存中）。
这样，一旦下一个 bean 创建的时候需要依赖 bean ，则直接使用 ObjectFactory 的 #getObject() 方法来获取了，也就是 「2.1 getSingleton」 小结中的方法中的代码片段了。
到这里，关于 Spring 解决 bean 循环依赖就已经分析完毕了。最后来描述下就上面那个循环依赖 Spring 解决的过程：

首先 A 完成初始化第一步并将自己提前曝光出来（通过 ObjectFactory 将自己提前曝光），在初始化的时候，发现自己依赖对象 B，此时就会去尝试 get(B)，这个时候发现 B 还没有被创建出来
然后 B 就走创建流程，在 B 初始化的时候，同样发现自己依赖 C，C 也没有被创建出来
这个时候 C 又开始初始化进程，但是在初始化的过程中发现自己依赖 A，于是尝试 get(A)，这个时候由于 A 已经添加至缓存中（一般都是添加至三级缓存 singletonFactories ），通过 ObjectFactory 提前曝光，所以可以通过 ObjectFactory#getObject() 方法来拿到 A 对象，C 拿到 A 对象后顺利完成初始化，然后将自己添加到一级缓存中
回到 B ，B 也可以拿到 C 对象，完成初始化，A 可以顺利拿到 B 完成初始化。到这里整个链路就已经完成了初始化过程了
老艿艿的建议

可能逻辑干看比较绕，胖友可以拿出一个草稿纸，画一画上面提到的 A、B、C 初始化的过程。

相信，胖友会很快明白了。

如下是《Spring 源码深度解析》P114 页的一张图，非常有助于理解。

处理依赖循环
处理依赖循环



可能是最漂亮的 Spring 事务管理详解
https://mp.weixin.qq.com/s?__biz=MzUzMTA2NTU2Ng==&mid=2247484702&idx=1&sn=c04261d63929db09ff6df7cadc7cca21&chksm=fa497aafcd3ef3b94082da7bca841b5b7b528eb2a52dbc4eb647b97be63a9a1cf38a9e71bf90&token=165108535&lang=zh_CN#rd


如何设计一个高并发系统？
可以分为以下 6 点：
系统拆分
缓存
MQ
分库分表
读写分离
ElasticSearch
