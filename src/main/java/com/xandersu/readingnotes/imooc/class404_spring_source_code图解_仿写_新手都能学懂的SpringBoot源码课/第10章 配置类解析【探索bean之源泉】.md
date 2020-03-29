# 第10章 配置类解析

- 全局流程解析
- 执行逻辑解析
- 核心方法解析
- 章节总结

# 配置类解析入口

1. refresh
2. invokeBeanFactoryPostProcessors
3. ConfigurationClassPostProcessor
4. postProcessBeanDefinitionRegistry



## postProcessBeanDefinitionRegistry逻辑

1. 获取BeanDefinitionRegistry唯一id:registryId
2. 检查registryId是否处理过
3. 添加registryId到已处理集合中
4. processConfigBeanDefinitions

## processConfigBeanDefinitions逻辑

开始 => 遍历BeanDefinition => 是否处理过（是，不为full/lite；是） => 检查ConfigurationClass属性 => 为full/lite => 添加至configCandidates集合 => 对集合按order进行排序 => 遍历canfigCandidates集合进行解析处理 => 遍历importRegistry及清空缓存 => 结束



## 执行逻辑解析

