# 6-01 Java

- 平台无关性
- GC
- 语言特性 （泛型，反射，lambda）
- 面向对象
- 类库
- 异常处理

# 平台无关性，compile once,Run any way如何实现

- 编译时 
  - javac编译成class
  - java 运行
  - javap -c 反编译
- 运行时

java源码首先被编译成字节码，再由不同平台的JVM进行解析，Java语言在不同平台上进行时不需要进行重新编译，Java虚拟机在执行字节码的时候，把字节码转换成具体平台上的机器指令。

# JVM如何加载.class文件

## Java虚拟机

- class loader:依据特定格式，加载class文件到内存
- execution engine：对命令进行解析
- native interface:融合不同开发语言的原生库作为java所用
- runtime data area :java内存空间结构模型

# 反射

 Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为反射。

# ClassLoader

- 编译器将robot.java源文件编译成为robot.class字节码文件
- ClassLoader将字节码转换成JVM中的Class<Robot>对象
- JVM利用Class<Robot>对象实例化Robot对象

classloader非常重要，主要工作在Class装载的加载阶段，作用是从系统外部获得Class二进制数据流。是Java核心组件，所有class都是由ClassLoader进行装载的，ClassLoader负责通过将class文件里的二进制数据流装载进系统，然后交给Java虚拟机进行连接，初始化等操作。

# ClassLoader的种类

- bootstrapClassLoader：C++编写，加载核心库java.*
- extClassLoader：Java编写，加载扩展库javax.*
- AppClassLoader：java编写，加载程序所在目录
- 自定义ClassLoader：java编写，定制化加载

# 自定义ClassLoader的实现

关键函数：findClass,defineClass

# ClassLoader的双亲委派机制

1. 自底向上检查类是否已经被加载
2. 自顶向下尝试加载类

- 避免多份同样字节码的加载

# 类加载方式

- 隐式加载：new
- 显式加载：loadClass,forName等

# loadClass,forName区别

类加载过程：

1. 加载：
   - 通过ClassLoader加载Class文件字节码，生成Class对象
2. 链接：
   - 校验：检查加载的class的正确性和安全性
   - 准备：为类变量分配存储空间并设置类变量初始值
   - 解析：JVM将常量池内的符号引用转换为直接引用
3. 初始化：
   - 指令类变量赋值和静态代码块



- Class.forName得到的Class是已经被初始化完成的
- Class.loadClass得到的Class是还没有链接的

# java内存模型

