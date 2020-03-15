# SpringBoot Bean解析

# 章节目录

1. IOC思想
2. xml配置注入
3. 注解方式注入
4. refresh方法（Spring核心方法）
5. bean实例化解析

# IOC思想

松耦合

灵活性

可维护

## Bean配置方式

- xml
- 注解

## XML方式配置Bean

1. 无参构造
2. 有参构造
3. 静态工厂方法
4. 实例工厂方法

### 无参构造

```
<bean id="student" class="com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Student">
    <property name="name" value="zhangsan"/>
    <property name="age" value="18"/>
    <property name="classList">
        <list>
            <value>math</value>
            <value>english</value>
        </list>
    </property>
</bean>
```

### 有参构造

```
<bean id="student" class="com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.Student">
    <constructor-arg index="0" value="zhangsan"/>
    <constructor-arg index="1" value="13"/>
</bean>
```

### 静态工厂方法

```
public class AnimalFactory {

    public static Animal getAnimal(String type) {
        if ("dog".equals(type)) {
            return new Dog();
        } else if ("cat".equals(type)) {
            return new Cat();
        }
        return null;
    }
}

 <bean id="dog" class="com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.AnimalFactory"
          factory-method="getAnimal">
        <constructor-arg value="dog"/>
    </bean>
```

### 实例工厂方法

```
public class AnimalFactory {

    public static Animal getAnimal(String type) {
        if ("dog".equals(type)) {
            return new Dog();
        } else if ("cat".equals(type)) {
            return new Cat();
        }
        return null;
    }
}


<bean id="animalFactory2" class="com.xandersu.readingnotes.imooc.class404_spring_source_code.IOC.xml.AnimalFactory2">
</bean>

<bean id="dog2" factory-method="getAnimal"
      factory-bean="animalFactory2">
    <constructor-arg value="dog"/>
</bean>
```

### 优点

- 低耦合
- 对象依赖关系清晰
- 集中管理

#### 缺点

- 配置繁琐
- 开发效率较低
- 文件解析耗时



## 注解方式配置Bean

1. @Component
2. 配置类中使用@Bean
3. 实现FactoryBean
4. 实现BeanDefinitionRegistryPostProcessor
5. 实现ImportBeanDefinitionRegistrar



### @Component

```
@Component
public class HelloService2 {}
```

### 配置类中使用@Bean

```
@Configuration
public class BeanConfig {

    @Bean("dog3")
    public Animal getDog3(){
        return new Dog();
    }
}
```

### 实现FactoryBean

```
@Component
public class MyCat implements FactoryBean<Animal> {
    @Override
    public Animal getObject() throws Exception {
        return new Cat();
    }

    @Override
    public Class<?> getObjectType() {
        return Animal.class;
    }
}
```

### 实现BeanDefinitionRegistryPostProcessor

```
@Component
public class MyBeanRegister implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(Monkey.class);
        registry.registerBeanDefinition("monkey", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
```

### 实现ImportBeanDefinitionRegistrar

```
@Component
public class MyBeanImport implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(Bird.class);
        registry.registerBeanDefinition("bird", beanDefinition);
    }
}

@Import(MyBeanImport.class)
```

### 优点

- 使用简单
- 开发效率高
- 高内聚

### 缺点

- 配置分散
- 对象关系不清晰
- 配置修改需要重新编译工程



# Spring框架核心方法refresh解析

1. bean配置读取加载入口
2. spring框架启动流程
3. 面试重点

org.springframework.context.support.AbstractApplicationContext#refresh

start：

=> prepareRefresh(); 

=> obtainFreshBeanFactory(); 

=> prepareBeanFactory(beanFactory); 

=> postProcessBeanFactory(beanFactory); 

=> invokeBeanFactoryPostProcessors(beanFactory); 

=> registerBeanPostProcessors(beanFactory); 

=> initMessageSource(); 

=> initApplicationEventMulticaster(); 

=>onRefresh();  

=> registerListeners(); 

=> finishBeanFactoryInitialization(beanFactory); 

=> finishRefresh(); 

=> resetCommonCaches();

异常 => destroyBeans(); => cancelRefresh(ex);



##  prepareRefresh方法

- 容器状态设置
- 初始化属性设置
- 检查必备属性是否存在

##  obtainFreshBeanFactory方法

- 设置BeanFactory的序列化id
- 获取BeanFactory

##  prepareBeanFactory方法

- 设置BeanFactory的一些属性
- 添加后置处理器
- 设置忽略的自动装配接口
- 注册一些组件

##  postProcessBeanFactory方法

- 子类重写以在BeanFactory完成创建后作进一步设置

## invokeBeanFactoryPostProcessors方法

#### 步骤1

遍历BeanFactoryPostProcessors => 是否实现了BeanDefinitionRegistryPostProcessor

是 => 调用postProcessBeanDefinitionRegistry方法 => 添加至RegistryPostProcessors集合中

否 => 添加至regularPostProcessor集合中

#### 步骤2

遍历BeanFactory中所有BeanDefinitionRegistryPostProcessor实现 => 是否实现了PriorityOrder接口

是 => 添加至currentRegistryProcessors集合中 => 添加至processedBeans集合中

否 => 对集合currentRegistryProcessors进行排序 => 将上述结果添加至RegistryProcessors集合中 => currentRegistryProcessors结合内对象依次调用postProcessBeanDefinitionRegistry方法 => 清空集合currentRegistryProcessors

#### 步骤3

遍历BeanFactory中所有BeanDefinitionRegistryPostProcessor实现 => 若未处理过并且实现了Ordered接口

是 => 添加至currentRegistryProcessors集合中 => 添加至processedBeans集合中

否 => 对集合currentRegistryProcessors进行排序 => 将上述结果添加至RegistryProcessors集合中 => currentRegistryProcessors集合内对象依次调用postProcessBeanDefinitionRegistry方法 => 清空集合currentRegistryProcessors

#### 步骤4

循环遍历步骤3直至BeanFactory不存在未处理的BeanDefinitionRegistryPostProcessor实现 => registryProcessors集合内对象依次调用postProcessBeanDefinitionRegistry方法  => regularPostProcessor集合内对象依次调用postProcessBeanDefinitionRegistry方法

### 作用

- 调用BeanDefinitionRegistryPostProcessor实现向容器内添加bean的定义
- 调用BeanFactoryPostPostProcessor实现向容器内bean的定义添加属性

## registerBeanPostProcessors方法

- 找到所有BeanPostProcessor实现
- 排序后注册进容器内

## initMessageSource方法

国际化多语言的配置

- 初始化国际化相关的属性

## initApplicationEventMulticaster方法

- 初始化事件广播器

## onRefresh方法

空实现，用于子类实现

- 创建web容器（web环境）

## registerListeners方法

- 添加容器内事件监听器至事件广播器中
- 派发早期事件

## finishBeanFactoryInitialization方法

- 初始化所有剩下的单例bean

## finishRefresh方法

清空缓存

- 初始化生命周期处理器
- 调用生命周期处理器onRefresh方法
- 发送ContextRefreshedEvent事件
- JM相关X处理

## resetCommonCaches方法

- 清理缓存



# Bean实例化解析

## BeanDefination介绍

- 一个对象在Spring描述，RootBeanDefination是起常见实现
- 通过操作BeanDefination来完成bean的实例化和属性注入

## 自定义创建bean

createBean

resolveBeforeInstantiation

applyBeanPostProcessorsBeforeInstantiation

自定义实现PostProcessorsBeforeInstantiation

## 实例化流程

**start**

getBean

doGetBean

getSingleton

CreateBean

resolveBeforeInstantiation

doCreateBean

createBeanInstance

instantiateBean

instantiate

populateBean

initializeBean

**end**

# 面试题

### 介绍一下ioc思想

### springboot中bean有哪几种配置方式

### bean配置你最喜欢的方式

### 介绍refresh方法流程

### 列举refresh比较熟悉的方法说出作用

### 介绍bean实例化的流程

### 说几个bean实例化的扩展点及其作用