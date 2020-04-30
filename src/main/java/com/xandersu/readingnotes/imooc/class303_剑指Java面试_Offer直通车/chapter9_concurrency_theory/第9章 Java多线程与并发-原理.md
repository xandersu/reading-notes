# 第9章 Java多线程与并发-原理

# Synchronized

#### 线程安全的主要诱因

- 存在共享数据（也称为临界资源）
- 存在多条线程共同操作这些共享数据

#### 解决问题的根本方法：

同一时刻有且只有一个线程在操作共享数据，其他线程必须等到该线程处理完数据后再对共享数据进行操作。



## 互斥锁

#### 特性

1. 互斥性：即在同一时间只允许一个线程持有某个对象锁，通过这种特性来实现多线程的协调机制，这样在同一时间只有一个线程对需要同步的代码块（复合操作）进行访问。互斥性也被称为操作的原子性。
2. 可见性：必须确保在锁被释放之前，对共享变量所做的修改，对于随后获得该锁的另一个线程是可见的（即在获得锁时应获得最新共享变量的值），否则另一个线程可能是在本地缓存的某个副本上继续操作，从而引起不一致。

**synchronized锁的不是代码，锁的都是对象**



### 根据获取的锁的分类：获取对象锁和获取类锁

#### 获取对象锁的两种用法：

1. 同步代码块(synchronized(this)、synchronized(类实例对象))，锁的是小括号()中的实例对象。
2. 同步非静态方法（synchronized method），锁的是当前对象的实例对象。

#### 获取类锁的两种用法：

1. 同步代码块synchronized(类.class)，锁的是小括号()中的类对象（class对象）。
2. 同步非静态方法（synchronized method），锁的是当前对象的实例对象（class对象）。



### 对象锁和类锁的总结：

1. 有线程访问对象的同步代码块时，另外的线程可以访问该对象的非同步代码块。
2. 若锁住的是同一个对象，一个线程在访问对象的同步代码块时，另一个访问对象的同步代码块的线程会被阻塞。
3. 若锁住的是同一个对象，一个线程在访问对象的同步方法时，另一个访问对象的同步方法的线程会被阻塞。
4. 若锁住的是同一个对象，一个线程在访问对象的同步代码块时，另一个访问对象的同步方法的线程会被阻塞，反之亦然。
5. 同一个类的不同对象的对象锁互不干扰。
6. 类锁由于也是一种特殊的对象锁，因此表现和上述1，2，3，4一致，而由于一个类只有一把对象锁，所以同一个类的不同对象使用类锁将会是同步的。
7. 类锁和对象锁互不干扰。



# synchronized底层实现原理

### 实现synchronized的基础

- Java对象头
- Monitor



### 对象在内存中的布局

- 对象头
- 实例数据
- 对齐填充



### 对象头的结构

| 虚拟机位数 |       头对象结构       |                             说明                             |
| :--------: | :--------------------: | :----------------------------------------------------------: |
|  32/64位   |       Mark Word        |   默认存储对象的hashCode，分代年龄，锁类型，锁标志位等信息   |
|  32/64位   | Class Metadata Address | 类型指针指向元素对象的类元数据，JVM通过这个指针确定该对象是哪个类的数据 |



### Mark Word

由于对象头的信息是与对象自身定义的数据没有关系的额外存储成本，因此，考虑到JVM的空间效率，Mark Word被设计成为一个非固定的数据结构，以便存储更多有效的数据。根据对象本身的状态复用自己的存储空间。



锁状态  25bit     4 bit    1bit    2bit

​            23bit  2bit         是否是偏向锁   锁标志位

无状态锁   对象hashCode、对象分代年龄     01

轻量级锁   指向锁记录的指针                         00

重量级锁    指向重量级锁的指针                10

GC标志       空，不需要记录信息                   11

偏向锁  线程id  Epoch  对象分代年龄   1    01



### Monitor：每个Java对象天生自带了一把看不见的锁

管程、监视器锁   理解成一个同步工具，描述为一种同步机制， 通常描述为一个对象

 Monitor是由ObjectMonitor实现的。位于hotspot源码内，C++实现。

ObjectMonitor{

owner ：指向持有ObjectMonitor对象的线程

entryList：

waitSet：

count： 计数器

}

Monitor对象存在于每个Java对象的对象头当中

entryList     owner     waitSet

enter ， acquire  ，  release ， acquire ，  release and exit



### 什么是重入

从互斥锁的设计上来说，当一个线程试图操作一个由其他线程持有的对象锁的临界资源时，将会处于阻塞状态，但当一个线程再次请求自己持有对象锁的临界资源时，是允许的，这种情况属于重入。



为了保证在方法异常完成时，monitorenter和monitorexit依然可以正确配对执行，编译器会自动产生一个异常处理器，这个异常处理器声明可处理所有异常，目的是用来执行monitorexit指令。



#### synchronized问题

- Java早期版本中，synchronized属于重量级锁，底层依赖于Mutex Lock实现。
- 线程之间的切换需要从用户态切换到核心态，开销较大。

#### synchronized优化

- 自适应自旋
- 锁消除
- 锁粗化
- 轻量级锁
- 偏向锁



## 自旋锁和自适应自旋锁

### 自旋锁

- 很多情况下，共享数据的锁定状态持续时间较短，，为了这段时间挂起和恢复阻塞线程、切换线程不值得
  - 在多处理器的环境下，可以让另外没有获取到锁的线程等待一会但不放弃CPU执行时间
  - 等待一会但不放弃CPU执行时间就是**自旋**
- 通过让线程执行忙循环等待锁的释放，不让出CPU
  - while(true)
  - 不像Sleep一样放弃CPU执行时间
  - JDK4引入默认关闭，JDK6默认开启，
- 如果占用时间短效果很好
- 缺点：如果锁被其他线程长时间占用，会带来很多性能上的开销
  - 线程自旋时，始终占用CPU的时间片
  - preBlockSpin参数更改自旋后锁升级的次数

### 自适应自旋锁

JDK6引入

- 自旋的次数不确定
- 由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定
  - 如果在同一个锁对象上，自选等待刚刚成功获取过锁，并且持有锁的线程正在运行中，那么JVM会认为该锁自旋获取到锁的概率很大，会自动增加等待时间。相反，如果对于某个锁自旋很少成功获取到锁，那在以后要获取锁时，将可能省略掉自旋过程，以避免浪费处理器资源。

### 锁消除

更彻底的优化

- JIT编译时，对运行上下文进行扫描，去除不可能存在竞争的锁



### 锁粗化

另一个极端

- 扩大加锁范围，避免反复加锁解锁



## synchronized的四种状态

- 无锁、偏向锁、轻量级锁、重量级锁
- 锁膨胀方向：无锁 => 偏向锁 => 轻量级锁 => 重量级锁



## 偏向锁

- 大多数情况下，锁不存在多线程竞争，总是由同一个线程多次获得

#### 核心思想：

如果一个线程获得了锁，那么锁就进入偏向模式，此时Mark word 的结构也变为偏向锁结构，当该线程再次请求锁，无需在做同步操作，即获取锁的过程只需要检查Mark word 的锁标记位为偏向锁以及当前线程ID等于Mark word 的Thread ID即可，这样就省去了大量有关锁申请的操作。

不适合用于锁竞争比较激烈的多线程场合



## 轻量级锁

轻量级锁是由偏向锁升级来的，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁。

适应场景：线程交替执行同步块

若存在同一时间访问同一锁的情况，就会导致轻量级锁膨胀为重量级锁。

### 轻量级锁的加锁过程

1. 在代 码即将进入同步块的时候，如果此同步对象没有被锁定(锁标志位为“ 01”状态)，虚拟机 首先将在当前线程的钱帧中建立 一个名为锁记录( Lock Record)的空 间 ，用于存储锁对象 目前的 Mark Word 的拷贝 (官方为这份拷贝 加了一个 Displaced 前缀，即 Displaced Mark Word)，这时候线程堆楼与对象头的状态如图 13-3 所示 。
2. 拷贝对象头中的 Mark Word 复制到锁记录中。
3. 然后，虚拟机将使用 CAS 操作尝试把对象的 Mark Word 更新为指向 Lock Record 的指针。并将Lock Record里的owner指针指向object mark word。
4. 如果这个更新动作成功了，即代表该线程拥有了这个对象的锁，并且对象 Mark W ord 的锁标志位( Mark Word 的最后两个比特)将转变为“00”，表示此对象处于轻量级锁定状 态 。
5. 如果这个更新操作失败了，那就意味着至少存在一条线程与当前线程竞争获取该对象 的锁 。 虚拟机首先会检查对象的 Mark Word 是否指向当前线程的栈帧，如果是，说明当前线程已经拥有了这个对象的锁，那直接进入同步块继续执行就可以了，否则就说明这个 锁对象已经被其他线程抢占了 。 如果出现两条以上的 I 线程争用同一个锁的情况，那轻量级锁就不再有效，! 必须要膨胀为重 量 级锁，锁标志的状态值变为“ l。”， 此时 Mark Word 中存储的就是指向重 量级 锁(互斥 量) 的指针，后面等待锁的线程也必须进入阻 塞状态 。

### 轻量级锁的解锁过程

1. 通过 CAS 操作来进行的，如果对象的 Mark Word仍然指向线程的锁记录，那就用 CAS操作把对图 13-4 象当前的 Mark Word 和线程中复制的 Displaced Mark Word 替换回来 。 
2. 假如能够成功替换，那整个同步过程就顺利完成了;
3. 如果替换失败，则说 明有其他线程尝试过获取该锁，就要在释放锁的同时，唤醒被挂起的线程。



### 锁的内存语义

当线程释放锁时，Java内存模型会把该线程对应的本地内存中的共享变量刷新到主存中。

而当线程获取锁时，Java内存模型会把该线程对应的本地内存置为无效，从而使得被监视器保护的临界区代码必须从主内存中读取共享变量。



### 偏向锁、轻量级锁、重量级锁的汇总

| 锁       | 优点                                                         | 缺点                                                         | 使用场景                                         |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------ |
| 偏向锁   | 加锁和解锁不需要CAS操作，没有额外的性能消耗，和执行非同步方法相比仅存在纳秒级的差距 | 如果线程间存在锁竞争，会带来额外的锁撤销的消耗               | 只有一个线程访问同步块或者同步方法的场景         |
| 轻量级锁 | 竞争的线程不会阻塞，提高了响应速度                           | 若线程长时间抢不到锁，自旋会消耗CPU性能                      | 线程交替执行同步块或者同步方法的场景             |
| 重量级锁 | 线程竞争不使用自旋，不会消耗CPU                              | 线程阻塞，响应时间缓慢，再多线程下，频繁的获取释放锁，会带来巨大的性能消耗 | 追求吞吐量，同步块或者同步方法执行时间较长的场景 |



# synchronized和ReentrantLock的区别

ReentrantLock可重入锁

- 位于java.util.concurrent.locks包下
- 和countDownLatch、FutureTask、Semaphore一样基于AQS实现
- 能够实现比synchronized更细粒度的控制，如控制fairness 公平锁
- 调用lock()，必须调用unlock()释放锁
- 性能未必比synchronized高，也是可重入的

### ReentrantLock公平性的设置

- ReentrantLock fairLock = new ReentrantLock(true);
- 参数为true时，倾向于将锁赋予等待时间最久的线程。
  - 公平性是减少线程饥饿情况发生的办法
  - 线程饥饿：个别线程长期等待锁，但却始终无法获得锁
- 公平锁：获取锁的顺序按先后调用lock方法的顺序（慎用）
- 非公平锁：抢占顺序不一定，和等待时间无关
- synchronized是非公平锁



### ReentrantLock将锁对象化

- 判断是否有线程，或者某个特定线程，在排队等待锁
  - 感知哪些线程在争抢锁
- 带超时的获取锁的尝试
- 感知到有没有成功获取到锁



### 能否将wait\notify\notifyAll对象化

- java.util.concurrent.locks.Condition

### 总结

- synchronized是关键字，ReentrantLock是类
- ReentrantLock可以对获取锁的等待时间进行设置，避免死锁
- ReentrantLock可以获取各种锁的信息
- ReentrantLock可以灵活地实现多路通知
- 机制：synchronized操作Mark Word，ReentrantLock调用UnSafe类的park()方法



# Java内存模型（JMM）

### Java内存模型

Java Memory Model，简称JMM，本身是一种抽象的概念，并不真实存在，他描述的是一组规则或者规范，通过这组规范定义了程序中各个变量（包括实例字段，静态字段和构成数组对象的元素）的访问方式。



### JMM中的主内存

- 存储Java实例对象
- 包括类成员变量、类信息、常量、静态变量等
- 属于数据共享的区域，多线程并发操作时会引发线程安全问题



### JMM的工作内存

- 存储当前方法的所有本地变量信息，本地变量对其他线程不可见。
  - 存储的主内存中的变量副本的拷贝，每个线程只能访问自己的工作内存，即线程中的本地变量对其他线程不可见。
- 字节码行号指示器，Native方法信息
- 属于线程私有数据区域，不存在线程安全问题



### JMM和Java内存区域划分是不同的概念层次

- JMM描述的是一组规则，围绕原子性、有序性、可见性展开
- 相似点：都存在共享区域和私有区域
  - JMM的主内存属于共享数据区域，从某种角度上讲，包含了堆和方法区
  - 工作内存数据线程私有数据区域，从某种角度上讲，应该包括程序计数器、虚拟机栈、本地方法栈



### 主内存和工作内存的数据存储类型以及操作方式归纳

- 方法里的基本数据类型本地变量将直接存储在工作内存的栈帧结构中
- 引用类型的本地变量：引用存储在工作内存中，对象实例存储在主内存（共享数据区域，堆）中
- 实例对象的成员变量、static变量、类信息均会被存储在主内存中
  - 实例对象的成员变量，不管是基本数据类型或者是包装类型还是引用类型，都会被存储到主内存的堆中
  - static变量、类信息也存储在主内存中
- 主内存共享的方式是线程各拷贝一份数据到工作内存中，操作完成后刷新会主内存



### 指令重排序需要满足的条件

在执行程序的时候，为了提高性能，处理器和编译器常常会对指令进行重排序，但是不能随意的。

- 在单线程环境下不能改变程序运行的结果。
- 存在数据依赖关系的不允许重排序。

**无法通过happens-before原则推导出来的，才能进行指令重排序**



### A操作的结果需要对B操作可见，则A与B存在happens-before关系



## happens-before原则

1. **程序次序原则**：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作；
   - 准确的说，应该是控制流程序而不是程序代码顺序，因为要考虑分支、循环等结构；
2. **锁定规则**：一个unlock操作先行发生于后面对同一个锁的lock操作
   - 这里必须强调的是同一个锁，“后面”指的是时间上的先后顺序。
3. **volatile变量规则**：对一个变量的写操作先行发生于后面对这个变量的读操作。
   - “后面”同样是时间上的先后顺序。
   - 加内存屏障
4. **传递规则**：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C。
5. **线程启动规则**：Thread对象的start()方法先行发生于此线程的每个一个动作。
6. **线程中断规则**：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生。
7. **线程终结规则**：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值手段检测到线程已经终止执行。
8. **对象终结规则**：一个对象的初始化完成先行发生于他的finalize()方法的开始。



### happens-before的概念

如果两个操作不满足上述任意一个happens-before规则，那么这两个操作就没有顺序的保障，JVM可以对这两个操作进行重排序；

如果操作A happens-before 操作B，那么操作A在内存上所做的操作对操作B都是可见的。



## Volatile：JVM提供的轻量级同步机制

- 保证被Volatile修饰的共享变量对所有线程都是可见的
- 禁止指令的重排序优化



### Volatile的可见性

被Volatile修饰的变量，对所有线程总是立即可见的，对Volatile变量的所有写操作总是能立即反应到其他线程中。

但是对Volatile变量运算操作在多线程环境中并不保证安全性。



### Volatile变量为何立即可见？

当写一个Volatile变量时，JMM会把该线程对应的工作内存中的共享变量值刷新到主内存中；

当读取一个Volatile变量时，JMM会把该线程对应的工作内存置为无效。



### Volatile如何禁止重排序优化

#### 内存屏障（Memory Barrier）

1. 保证特定操作的执行顺序
2. 保证某些变量的内存可见性

通过插入内存屏障指令禁止在内存屏障前后的指令执行重排序优化。

强制刷出个版本CPU的缓存数据，因此任何CPU上的线程都能读取到这些数据的最新版本。



memory = allocate();//1.分配对象内存空间

instance(memory);//2.初始化对象

instance = memory;//3、设置instance指向刚分配的内存地址



## Volatile和synchronized的区别

1. Volatile本质实在告诉JVM当前变量在寄存器（工作内存）中的值是不确定的，需要从主内存中读取；synchronized则是锁定当前变量，只有当前线程可以访问该变量，其他线程被阻塞住直到该线程完成变量操作为止。
2. Volatile仅能使用在变量级别；synchronized则可以使用在变量、方法和类级别。
3. Volatile仅能实现变量的修改可见性，不能保证原子性；而synchronized则可以保证变量修改的可见性和原子性。
4. Volatile不会造成线程的阻塞；synchronized可能会造成线程的阻塞
5. Volatile标记的变量不会被编译器优化；synchronized标记的变量可以被编译器优化。



# CAS（Compare And Swap）

一种高效实现线程安全性的方法

- 支持原子更新操作，适用于计数器，序列发生器等场景
- 属于乐观锁机制，号称lock-free
- CAS操作失败时由开发者决定是继续尝试，还是执行别的操作



#### 包含三个操作数——内存位置（V）、预期原值（A）、和新值（B）



**CAS多数情况下对开发者是透明的**

- JUC的atomic包提供了常用的原子性数据类型以及引用、数组等相关原子类型和更新操作工具，是很多线程安全程序的首选
- UnSafe类虽然提供CAS服务，但是因为能够操作任意内存地址读写而有隐患
- JDK9之后，可以使用Variable Handle API来代替UnSafe

### 缺点

- 若循环时间长，则CPU自旋开销很大
- 只能保证一个共享变量的原子操作
- ABA问题  =>  解决AtomicStampedReference提供版本号



# 线程池

为解决资源分配这个问题，线程池采用了“池化”（Pooling）思想。池化，顾名思义，是为了最大化收益并最小化风险，而将资源统一在一起管理的一种思想。

### 利用Executors创建不同的线程池满足不同场景的需求

1. newFixedThreadPool(int nThreads) 

   指定工作线程数量的线程池

2. newCachedThreadPool()处理大量短时间工作任务的线程池

   1. 试图缓存线程并重用，当无缓存线程可用时，就会创建新的工作线程；
   2. 如果线程闲置的时间超过阈值，则会被终止并移出缓存
   3. 系统长时间闲置不用时，不会消耗什么资源

3. new SingleThreadExecutor()  

   创建唯一的工作线程来执行任务，如果线程异常结束，会有另一个线程取代他

4. new SingleThreadScheduledExecutor()与 newScheduledThreadPool(int corePoolSize)  

   定时或者周期性的工作调度，两者的区别在于单一工作线程还是多个线程

5. new WorkStealingPool()

   内部会构建ForkJoinPool，利用working-stealing算法，并行地处理任务，不保证处理顺序



### Fork/Join框架

JDK7开始提供

- 把大任务分割成若干个小任务并行执行，最终汇总每个小任务结果后得到大任务结果的框架
  - Executors接口的具体实现，目的是更好的利用多处理器
  - 用于可以被递归的拆解成子任务的工作类型设计
  - 目的是运用所有可用的运算能力来提高性能
  - map reduce
  - 使用工作窃取，work stealing算法（某个线程从其他队列里窃取任务来执行）。
  - 完成工作任务的从其他正在执行的线程中窃取工作任务。
  - 使用双端队列，被窃取的线程从双端队列头部拿取任务，窃取线程从双端队列尾部拿取任务。



### 为什么使用线程池

- **降低资源消耗**：通过池化技术重复利用已创建的线程，降低线程创建和销毁造成的损耗。
- **提高响应速度**：任务到达时，无需等待线程创建即可立即执行。
- **提高线程的可管理性**：线程是稀缺资源，如果无限制创建，不仅会消耗系统资源，还会因为线程的不合理分布导致资源调度失衡，降低系统的稳定性。使用线程池可以进行统一的分配、调优和监控。
- **提供更多更强大的功能**：线程池具备可拓展性，允许开发人员向其中增加更多的功能。比如延时定时线程池ScheduledThreadPoolExecutor，就允许任务延期执行或定期执行。



### Executor的框架



![JbssIO.png](https://s1.ax1x.com/2020/04/30/JbssIO.png)

Executor框架是根据一组执行策略调用、调度、执行和控制的异步任务框架。

目的是提供一种将任务提交、任务如何运行分离开来的机制。

JUC的三个Executor接口

- Executor：运行新任务的简单接口，将任务提交和任务执行细节解耦

  - ```
    void execute(Runnable command);
    ```

  - 将任务提交和任务执行进行解耦。用户无需关注如何创建线程，如何调度线程来执行任务，用户只需提供Runnable对象，将任务的运行逻辑提交到执行器(Executor)中，由Executor框架完成线程的调配和任务的执行部分。

  - 根据不同的实现。可能是创建一个线程并立即启动，也可能是使用已有的工作线程来运行传入的任务；也可能是使用已有的工作线程来运行传入的任务；也可能是根据线程池的容量或者阻塞队列的容量来决定是否要将传入的线程放入阻塞队列中，或者拒绝接受传入的任务。

- ExecutorService：扩展了Executor，具备管理执行器和任务生命周期的方法，提交任务机制更完善。

  - submit( callable )返回Future而不是void
  - shutdown()、isShutdown()管理方法
  - （1）扩充执行任务的能力，补充可以为一个或一批异步任务生成Future的方法；
  - （2）提供了管控线程池的方法，比如停止线程池的运行。

- ScheduledExecutorService：支持Future和定期执行任务。

  - 扩展了ExecutorService



## ThreadPoolExecutor



![JbH11I.png](https://s1.ax1x.com/2020/04/30/JbH11I.png)





###  java.util.concurrent.ThreadPoolExecutor.Worker

```
private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable{
				/** Thread this worker is running in.  Null if factory fails. */
        final Thread thread;  //Worker持有的线程
        /** Initial task to run.  Possibly null. */
        Runnable firstTask;    //初始化的任务，可以为null
}
```

Worker这个工作线程，实现了Runnable接口，并持有一个线程thread，一个初始化的任务firstTask。

- thread是在调用构造方法时通过ThreadFactory来创建的线程，可以用来执行任务；

- firstTask用它来保存传入的第一个任务，这个任务可以有也可以为null。如果这个值是非空的，那么线程就会在启动初期立即执行这个任务，也就对应核心线程创建时的情况；如果这个值是null，那么就需要创建一个线程去执行任务列表（workQueue）中的任务，也就是非核心线程的创建。

### ThreadPoolExecutor构造函数

- int corePoolSize：核心线程数量
- int maximumPoolSize：线程不够用时能够创建的最大线程数
- long keepAliveTime：线程空闲生存时间，当线程数量大于corePoolSize，如果没有新的任务提交，核心线程外的线程不会立即销毁，直到等待时间超过keepAliveTime才会被销毁
- TimeUnit unit
- BlockingQueue<Runnable> workQueue：任务等待队列
- ThreadFactory threadFactory：创建新的线程。
  - 默认Executors.defaultThreadFactory()，新创建的线程有同样的优先级，非守护线程，并且设置线程的名称
- RejectedExecutionHandler handler：线程池的饱和策略。
  - 如果阻塞队列满了并且没有空闲的线程，并且线程池中的线程数目达到maximumPoolSize时，这是如果继续提交任务，这是就需要一种策略来处理任务。4种策略。
  - AbortPolicy：直接抛出异常，这是默认策略。在任务不能再提交的时候，抛出异常，及时反馈程序状态。如果是比较关键的业务，推荐使用此拒绝策略，在系统不能承担更大的并发量的时候，能够及时的通过异常发现。
  - CallerRunsPolicy：用调用者（提交任务的线程）所在的线程来执行任务。这种情况是需要让所有任务都执行完毕，适合大量计算的任务去执行，多线程仅仅是增大吞吐量的手段，最终手段是让每个任务都执行完毕。
  - DiscardOldestPolicy：丢弃队列中最靠前的任务，并执行当前任务。
  - DiscardPolicy：直接丢弃任务。
  - 可以实现java.util.concurrent.RejectedExecutionHandler接口自定义handler



### 新任务提交execute执行后的判断

1. 首先检测线程池运行状态，如果不是RUNNING，则直接拒绝，线程池要保证在RUNNING的状态下执行任务。
2. 如果workerCount < corePoolSize，则创建并启动一个线程来执行新提交的任务，即使线程池中的其他线程是空闲的
3. 如果线程池中的线程数量大于等于corePoolSize且小于maximumPoolSize，则只有当workQueue满时才创建新的线程去处理任务
4. 如果设置的corePoolSize和maximumPoolSize相同，则创建的线程池的大小是固定的，这时如果有新任务提交，若workQueue未满，则将请求放入workQueue中，等待有空闲的线程去从workQueue中取任务并处理
5. 如果运行的线程数量大于等于maximumPoolSize，这时如果workQueue已经满了，则通过handler所指定的策略来处理任务



[![JqSKqs.png](https://s1.ax1x.com/2020/04/30/JqSKqs.png)](https://imgchr.com/i/JqSKqs)



### 线程池的状态

- RUNNING：能接受新提交的任务，并且也能处理阻塞队列中的任务
- SHUTDOWN：不能再接受新提交的任务，但可以处理存量任务
  - 在RUNNING状态下调用shutdown()方法会进入这种状态
- STOP：不再接受新提交的任务，也不处理存量任务，会中断正在处理任务的线程
  - 在RUNNING或者SHUTDOWN状态下调用shutdownNow()方法会进入这种状态
- TIDYING：所有任务都已经终止，workerCount（有效线程数）为0
- TERMINATED：terminated()方法执行完后进入该状态。
  - TIDYING状态后调用terminated()方法执



[![JbjM4S.png](https://s1.ax1x.com/2020/04/30/JbjM4S.png)](https://imgchr.com/i/JbjM4S)



线程池内部使用一个变量维护两个值：运行状态(runState)和线程数量 (workerCount)。在具体实现中，线程池将运行状态(runState)、线程数量 (workerCount)两个关键参数的维护放在了一起

```Java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
```

它同时包含两部分的信息：线程池的运行状态 (runState) 和线程池内有效线程的数量 (workerCount)，高3位保存runState，低29位保存workerCount，两个变量之间互不干扰。

```
private static final int COUNT_BITS = Integer.SIZE - 3;
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
// runState is stored in the high-order bits
private static final int RUNNING    = -1 << COUNT_BITS;
private static final int SHUTDOWN   =  0 << COUNT_BITS;
private static final int STOP       =  1 << COUNT_BITS;
private static final int TIDYING    =  2 << COUNT_BITS;
private static final int TERMINATED =  3 << COUNT_BITS;
// Packing and unpacking ctl
private static int runStateOf(int c)     { return c & ~CAPACITY; }  //计算当前运行状态
private static int workerCountOf(int c)  { return c & CAPACITY; }  //计算当前线程数量
private static int ctlOf(int rs, int wc) { return rs | wc; }  //通过状态和线程数生成ctl
```



### 线程池的大小如何选定

- CPU密集型：线程数=按照核数或者和数+1设定
- I/O密集型：线程数=CPU核数*（1+平均等待时间/平均工作时间）



《Java并发编程实战》



### 任务缓冲

任务缓冲模块是线程池能够管理任务的核心部分。

关键的思想就是将任务和线程两者解耦。

线程池中是以生产者消费者模式，通过一个阻塞队列来实现的。阻塞队列缓存任务，工作线程从阻塞队列中获取任务。

| 名称                  | 描述                                                         |
| --------------------- | ------------------------------------------------------------ |
| ArrayBlockingQueue    | 一个用数组实现的有界阻塞队列，此队列按照先进先出（FIFO）的原则对元素进行排序，支持公平锁和非公平锁。 |
| LinkedBlockingQueue   | 一个由链表结构组成的有界阻塞队列，此队列按照先进先出（FIFO）的原则对元素进行排序。此队列默认长度为Integer.MAX_VALUE，所以默认创建的该队列有容量危险。 |
| PriorityBlockingQueue | 一个支持线程优先级排序的无界队列，默认自然排序，也可以自定义实现compareTo()方法来指定元素排序规则，不能保证同优先级元素的顺序。 |
| DelayQueue            | 一个实现PriorityBlockingQueue实现延迟获取的无界队列，在创建元素时，可以指定多久才能从队列中获取当前元素。只有延时器满后才能从队列中获取元素。 |
| SynchronizedQueue     | 一个不存储元素的阻塞队列，每一个put操作必须等待take操作，否则不能添加元素。支持公平锁和非公平锁。SynchronizedQueue的一个使用场景是在线程池里。newCachedThreadPool()就使用了SynchronizedQueue，这个线程根据需要（新任务到来时）创建新线程，如果有空闲线程则会重复使用，线程空闲了60秒后会被回收。 |
| LinkedTransferQueue   | 一个由链表结构组成的无界阻塞队列，相当于其他队列，LinkedTransferQueue多了transfer和tryTransfer方法。 |
| LinkedBlockingQueue   | 一个由链表结构组成的双向阻塞队列。队列头部和尾部都可以添加和移除元素，多线程并发时，可以将锁的竞争最多降到一半。 |

