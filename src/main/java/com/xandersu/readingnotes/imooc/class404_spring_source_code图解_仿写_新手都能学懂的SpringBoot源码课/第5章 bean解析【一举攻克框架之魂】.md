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