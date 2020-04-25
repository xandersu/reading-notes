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

[![JsZt6U.png](https://s1.ax1x.com/2020/04/25/JsZt6U.png)](https://imgchr.com/i/JsZt6U)

1. 自底向上检查类是否已经被加载
2. 自顶向下尝试加载类

- 避免多份同样字节码的加载

### loadClass: 

```
protected Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException
{
		//同步锁，多个线程调用同一个classLoader加载同一个类，
    synchronized (getClassLoadingLock(name)) {
    		// 本身的classloader看有没有曾经加载过，加载过直接返回class
        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            long t0 = System.nanoTime();
            try {
                //父classLoader不为空
                if (parent != null) {
                		//调用父classLoader的loadClass方法
                		//extclassLoader的parent是bootstrapclassLoader是C++编写的所以为null
                    c = parent.loadClass(name, false);
                } else {
                    // 从bootstrapclassLoader里查找是否加载了类
                    c = findBootstrapClassOrNull(name);
                }
            } catch (ClassNotFoundException e) {
                // ClassNotFoundException thrown if class not found
                // from the non-null parent class loader
            }

            if (c == null) {
                // If still not found, then invoke findClass in order
                // to find the class.
                long t1 = System.nanoTime();
                //使用自己的自定义的findClass
                c = findClass(name);

                // this is the defining class loader; record the stats
                sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
}
```

自定义的classLoader的parent是AppClassLoader

AppClassLoader的parent是ExtClassLoader

ExtClassLoader的parent是null（bootstrapClassLoader，JVM内C++实现）

# 类加载方式

- 隐式加载：new
  - 程序运行过程中遇到通过new关键字生成对象时，隐式调用类加载器加载对应的类到JVM中
  - 无需调用class.newInstance()方法来生成对象的实例
  - new支持调用带参数的构造器生成对象实例
- 显式加载：loadClass，forName等
  - 获取到class对象后需要调用class.newInstance()方法来生成对象的实例
  - class.newInstance方法不支持传入参数，需要通过反射调用构造器的newInstance方法

# loadClass,forName区别

### 类装载过程：

1. 加载：
   
   - 通过ClassLoader加载Class文件字节码，生成Class对象
     - ClassLoader通过loadClass这个方法将class文件字节码加载到内存中，并将这些静态数据转换成运行时数据区中方法区的类型数据，在运行时，数据区堆中生成一个代表这个类的java.lang.class对象，作为方法区类数据的访问入口。
2. 链接：
   - 校验：检查加载的class的正确性和安全性

     - 检查class文件格式是否正确

   - 准备：为类变量分配存储空间并设置类变量初始值

     - 类变量（static变量）随类型信息存放在方法区中，生命周期很长，使用不当容易造成内存泄漏
     - 初始值指的是类变量类型的默认值而不是实际要赋的值

   - 解析（可选）：JVM将常量池内的符号引用转换为直接引用

     - ```
       resolveClass
       ```

     - 链接指定的类
3. 初始化：
   
   - 执行赋值和静态代码块



- Class.forName得到的Class是已经被初始化完成的

  - 链接MySql先加载驱动。需要使用Class.forName调用Driver类里的静态代码段

- Class.loadClass得到的Class是还没有链接的，只完成了加载

  - ioc框架加载classpath下的Bean，**延迟加载**，Spring ioc加快初始化速度，大量使用延迟加载，类的初始化动作留在实际使用中

  

# java内存模型

##内存简介：

逻辑地址-分段管理机制-线性地址-分页管理机制-物理地址

- 32位处理器：2^32的可寻址空间 大约4GB

- 64位处理器：2^64的可寻址空间 

JVM内存受限于于操作系统提供的可寻址地址空间

操作系统提供的可寻址地址空间由处理器的位数决定。

## 地址空间：

- 内核空间
  - 主要的操作系统程序和C运行时的空间，包含用于连接计算机硬件、调度程序以及提供联网和虚拟内存等服务的逻辑和基于C的进程。
- 用户空间
  - Java运行时内存实际使用的空间

## JVM内存模型-JDK8



[![JsdoZj.png](https://s1.ax1x.com/2020/04/25/JsdoZj.png)](https://imgchr.com/i/JsdoZj)



#### 线程私有：

- 程序计数器（字节码指令 no oom），
- 虚拟机栈（java方法 sof&oom），
  - JVM管理的，类似一个集合，但有固定的容量，由多个栈帧合起来
  - 每调用一个方法，Java虚拟机就会在内存中分配对应的一块空间，栈帧
  - 方法调用结束后，栈帧会销毁
  - 不需要GC，会自动释放
- 本地方法栈（native方法&sof&oom），

#### 线程共享：

- metaSpace（类加载信息oom），
- 【堆（数组和类对象oom）常量池（字面量和符号引用量oom）】，

### 程序计数器（Program Counter Register）

较小的内存空间、逻辑计数器而不是物理计数器线程独立、只为Java方法计数、没有oom

- 当前线程所执行的字节码行号指示器（逻辑）
- 改变计数器的值来选取下一条需要执行的字节码指令
  - 包括分支、循环、跳转、异常处理、线程恢复等基础功能。
- 和线程是一对一的关系，即线程私有
- 对Java方法计数，如果是native方法则计数器值为undefined
  - 如果是Java方法，计数器记录的是正在执行的虚拟机字节码指令的地址
- 不会发生内存泄漏

### Java虚拟机栈 stack

线程私有，方法调用结束时栈帧才会被销毁

- java方法执行的内存模型
- 包含多个栈帧
  - 局部变量表
  - 操作数栈
  - 动态连接
  - 返回地址（方法出口）

### 局部变量表和操作数栈

- 局部变量表：包含方法执行过程中的所有变量
  - this引用、所有方法参数、其他局部变量：boolean、byte、char、long、short、int、float、double等
- 操作数栈：入栈、出栈、复制、交换，产生消费变量
  - 在执行字节码指令过程中被用到
  - 类似于原生CPU寄存器
  - 大部分操作数栈把时间用在操作数的操作上
  - 因此局部变量的数组与操作数栈操作数据产生频繁执行

栈：前进后出

正在执行的方法在栈顶，每一次方法调用时，都会创建一个新的栈帧并压入栈顶

当方法正常返回或者抛出未捕获的异常时，栈帧就会出栈

栈除了压栈和出栈不能有其他操作

iload 压入操作数栈，入栈

istore出栈

javap -verbose

Jvm指令 局部变量表  操作数栈  程序计数器



[![JsNRRU.png](https://s1.ax1x.com/2020/04/25/JsNRRU.png)](https://imgchr.com/i/JsNRRU)

一个格子就是一个栈帧，七个栈帧

虚拟机栈会按照程序计数器从大到小依次压入栈中，执行时从小到大执行

1. iconst_0：将int值0压入操作数栈中，入参是1和2所以局部变量表两个变量第零个1，第一个2
2. istore_2：将操作数栈栈顶的元素0 pop出来，存入局部变量表的第二个变量种
3. iload_0：第零个局部变量表里的变量压入操作数栈栈顶
4. iload_1：第一个局部变量表里的变量压入操作数栈栈顶
5. iadd：将2和1弹出进行加计算然后将结果压入栈顶
6. istore_2：将栈顶元素3弹出放入局部变量表中的第二个位置
7. iload_2：将局部变量表第二个变量再次压入操作数栈栈顶
8. ireturn：将栈顶元素返回

局部变量表为操作数栈提供数据支撑。

### 递归为什么会引发java.lang.StackOverflowError栈溢出异常

递归过深，栈帧数超过虚拟栈深度

解决方法：限制递归次数、使用循环替代递归

每次调用一个方法都会创建一个栈帧，并压入栈中，放在栈顶，执行结束出栈

1. 递归每次调用一个方法都会创建一个栈帧
2. 保存当前方法的栈帧状态将他放入虚拟机栈中
3. 栈帧上下文切换时会切换到最新的方法栈帧中

### 虚拟栈过多会引发OutOfMemoryError

当虚拟机栈可以动态扩展时，如果无法申请到足够多的内存就会抛出OutOfMemoryError异常

### 本地方法栈

- 与虚拟机栈类似，主要作用于标注了native方法

### 元空间（meta Space），与永久代（permGen）区别

JDK8之后把类的元数据放在本地堆内存中，这块区域就是元空间meta space

这块区域在JDK7之前属于永久代

元空间和永久代都是存储class是相关信息，包括class对象的method、Field

元空间和永久代都是方法区的实现，实现不同

方法区是JVM规范，JDK7之后原来位于方法区里的字符串常量池被移动到了Java堆中

JDK8后使用元空间替代永久代

- **元空间使用本地内存，永久代使用的是jvm内存**
  java.lang.outofMemoryError:PermGen space 将不复存在

元空间使用本机内存、没有字符串常量池（移到堆中）

### 元空间（meta Space）相比永久代（permGen）优势

- 字符串常量池存在永久代中，容易出现性能问题和内存溢出
  - JDK6及之后的String类intern方法的区别
- 类和方法的信息大小难以确定，给永久代的大小指定带来困难
  - 太小永久代溢出、太大老年代溢出
- 永久代为gc带来不必要的复杂性，回收效率低
  - 在永久代中，元数据可能会随着Full GC发生而进行移动，hotspot的GC需要特殊处理永久代里的数据
  - 简化full gc
- 方便hotspot与其他JVM如jrockit的集成

### Java堆（heap）

JVM管理内存中最大的一块，是被所有线程共享，在虚拟机启动时创建，唯一目的是存放对象实例，几乎所有对象实例都在堆中分配内存。

JVM规范中Java堆可以在物理上不连续的内存空间中，只要逻辑上连续即可。即可固定大小也可以可扩展的（大部分都是可扩展的）

大小控制：-Xmx  -Xms

如果在堆中没有内存完成实例分配并且堆也无法再扩展时，就会抛出OutOfMemoryError异常

- 对象实例的分配区域
- GC管理的主要区域
  - 分代算法：新生代（eden、survivor1、survivor2）、老年代