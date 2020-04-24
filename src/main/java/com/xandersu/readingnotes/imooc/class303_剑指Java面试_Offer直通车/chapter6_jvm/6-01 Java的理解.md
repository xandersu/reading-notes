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

### 为什么JVM不直接将源码解析成机器码去执行

- 准备工作：每次执行都要各种检查
- 兼容性：也可以将别的语言解析成字节码

# JVM如何加载.class文件

## Java虚拟机

抽象化的计算机，通过在实际的计算机中仿真模拟各种计算机功能来实现，JVM自己完善的硬件架构，如处理器、堆栈、寄存器等，还具有相应的指令系统。JVM屏蔽了与具体操作系统平台相关的信息，使得Java程序只需生成在Java虚拟机上运行的目标代码，即字节码就可以在多种平台上不加修改的运行。

屏蔽与具体操作系统的不同

减少基于原生语言开发的复杂性

只要虚拟机厂商在特定操作系统上实现了虚拟机定义如何将字节码解析成本操作系统可执行的二进制码，java这门语言便能够实现跨越各种平台。

JVM内存模型和GC重点

内存中的虚拟机，JVM的存储就是内存，我们写的类、常量、变量、方法都在内存中，决定了我们的程序是否健壮、是否高效。

- Cass Loader:依据特定格式，加载class文件到内存
- execution engine：对命令进行解析
- native interface:本地接口，融合不同开发语言的原生库作为java所用
- runtime data area :java内存空间结构模型
  - stack 虚拟机栈
  - Heap 堆
  - Method Area：方法区
  - PC Regidter：PC指针
  - Native Method Stack：本地方法栈

# 反射

 Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为反射。

```
getDeclaredMethod 可以获取该类所有的方法，不能获取继承的或者实现的方法
getMethod 只能获取public方法，还能获取继承类、实现的接口的公用方法
```

# ClassLoader

- 编译器将robot.java源文件编译成为robot.class字节码文件
- ClassLoader将字节码转换成JVM中的Class<Robot>对象
- JVM利用Class<Robot>对象实例化Robot对象

classloader非常重要，主要工作在Class装载的加载阶段，作用是从系统外部获得Class二进制数据流。是Java核心组件，所有class都是由ClassLoader进行装载的，ClassLoader负责通过将class文件里的二进制数据流装载进系统，然后交给Java虚拟机进行连接，初始化等操作。



ClassLoader是抽象类、提供重要的接口用于自定义class的加载流程和加载方式

loadClass 给定类名去加载类，返回这个类的实例，找不到抛异常

# ClassLoader的种类

- bootstrapClassLoader：C++编写，加载核心库java.*

- extClassLoader：Java编写，加载扩展库javax.*

  - ```
    extends URLClassLoader 
    ```

  - 继承URLClassLoader。加载java.ext.dirs文件夹下的类。

  - ```
    System.getProperty("java.ext.dirs")
    ```

- AppClassLoader：java编写，加载程序所在目录，classpath类路径下

  - ```
    extends URLClassLoader 
    ```

  - ```
    System.getProperty("java.class.path")
    ```

- 自定义ClassLoader：java编写，定制化加载

# 自定义ClassLoader的实现

关键函数：findClass,defineClass

- findClass：寻找class文件，读进来二进制流做哪些处理。
  - 根据名称加载.class字节码，然后调用defineClass解析定义class字节流返回class对象
- defineClass：定义一个类



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

##内存简介：

逻辑地址-分段管理机制-线性地址-分页管理机制-物理地址

32位处理器：2^32的可寻址空间 

64位处理器：2^64的可寻址空间 

## 地址空间：

- 内核空间
- 用户空间

## JVM内存模型-JDK8

线程私有：程序计数器（字节码指令 no oom），虚拟机栈（java方法 sof&oom），本地方法栈（native方法&sof&oom）

线程共享：metaSpace（类加载信息oom），【堆（数组和类对象oom）常量池（字面量和符号引用量oom）】

### 程序计数器

- 当前线程所执行的字节码行号指示器（逻辑）
- 改变计数器的值来选取下一条需要执行的字节码指令
- 和线程是一对一的关系，即线程私有
- 对Java方法计数，如果是native方法则计数器值为undefined
- 不会发生内存泄漏

### Java虚拟机栈 stack

- java方法执行的内存模型
- 包含多个栈帧

### 局部变量表和操作数栈

- 局部变量表：包含方法执行过程中的所有变量
- 操作数栈：入栈、出栈、复制、交换，产生消费变量

javap -verbose

Jvm指令 局部变量表  操作数栈  程序计数器

### 递归为什么会引发栈溢出异常

递归过深，栈帧数超过虚拟栈深度

### 虚拟栈过多会引发OutOfMemoryError

### 本地方法栈

- 与虚拟机栈类似，主要作用于标注了native方法

### 元空间（meta Space），与永久代（permGen）区别

- **元空间使用本地内存，永久代使用的是jvm内存**
  java.lang.outofMemoryError:PermGen space



### 元空间（meta Space）相比永久代（permGen）优势

- 字符串常量池存在永久代中，容易出现性能问题和内存溢出
- 类和方法的信息大小难以确定，给永久代的大小指定带来困难
- 永久代为gc带来不必要的复杂性
- 方便hotspot与其他JVM如jrockit的集成

### Java堆（heap）

- 对象实例的分配区域
- GC管理的主要区域