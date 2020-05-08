     
[【死磕 Java 并发】—– J.U.C 之 AQS：阻塞和唤醒线程](/JUC/sike/aqs-3/ "【死磕 Java 并发】—– J.U.C 之 AQS：阻塞和唤醒线程")
===========================================================================================



1\. parkAndCheckInterrupt
=========================

在线程获取同步状态时，如果获取失败，则加入 CLH 同步队列，通过通过自旋的方式不断获取同步状态，但是在自旋的过程中，则需要判断当前线程是否需要阻塞，其主要方法在`acquireQueued(int arg)` ，代码如下：

// ... 省略前面无关代码  
  
if (shouldParkAfterFailedAcquire(p, node) &&  
 parkAndCheckInterrupt())  
 interrupted = true;  
  
// ... 省略前面无关代码  

*   通过这段代码我们可以看到，在获取同步状态失败后，线程并不是立马进行阻塞，需要检查该线程的状态，检查状态的方法为 `#shouldParkAfterFailedAcquire(Node pred, Node node)`方法，该方法主要靠前驱节点判断当前线程**是否应该被阻塞**。详细解析，在 [《【死磕 Java 并发】—– J.U.C 之 AQS：同步状态的获取与释放》](http://www.iocoder.cn/JUC/sike/aqs-2) 的 [「1.1.2 shouldParkAfterFailedAcquire」](#) 已经解析。
    
*   如果 `#shouldParkAfterFailedAcquire(Node pred, Node node)` 方法返回 **true** ，则调用`parkAndCheckInterrupt()` 方法，阻塞当前线程。代码如下：
    
    private final boolean parkAndCheckInterrupt() {  
     LockSupport.park(this);  
     return Thread.interrupted();  
    }  
    
    *   开始，调用 `LockSupport#park(Object blocker)` 方法，将当前线程挂起，此时就进入**阻塞等待**唤醒的状态。详细的实现，在 [「3\. LockSupport」](#) 中。
    *   然后，在线程被唤醒时，调用 `Thread#interrupted()` 方法，返回当前线程是否被打断，并清理打断状态。所以，实际上，线程被**唤醒**有两种情况：
        *   第一种，当前节点(线程)的**前序节点**释放同步状态时，唤醒了该线程。详细解析，见 [「2 unparkSuccessor」](#) 。
        *   第二种，当前线程被打断导致唤醒。

2\. unparkSuccessor
===================

当线程释放同步状态后，则需要唤醒该线程的后继节点。代码如下：

public final boolean release(int arg) {  
 if (tryRelease(arg)) {  
 Node h = head;  
 if (h != null && h.waitStatus != 0)  
 unparkSuccessor(h); // 唤醒后继节点  
 return true;  
 }  
 return false;  
}  

*   调用 `unparkSuccessor(Node node)` 方法，唤醒**后继**节点：
    
    private void unparkSuccessor(Node node) {  
     //当前节点状态  
     int ws = node.waitStatus;  
     //当前状态 < 0 则设置为 0  
     if (ws < 0)  
     compareAndSetWaitStatus(node, ws, 0);  
      
     //当前节点的后继节点  
     Node s = node.next;  
     //后继节点为null或者其状态 > 0 (超时或者被中断了)  
     if (s == null || s.waitStatus > 0) {  
     s = null;  
     //从tail节点来找可用节点  
     for (Node t = tail; t != null && t != node; t = t.prev)  
     if (t.waitStatus <= 0)  
     s = t;  
     }  
     //唤醒后继节点  
     if (s != null)  
     LockSupport.unpark(s.thread);  
    }  
    
    *   可能会存在当前线程的**后继**节点为 `null`，例如：超时、被中断的情况。如果遇到这种情况了，则需要跳过该节点。
        
        *   但是，为何是从 `tail` 尾节点开始，而不是从 `node.next` 开始呢？原因在于，取消的 `node.next.next` 指向的是 `node.next` 自己。如果顺序遍历下去，会导致**死循环**。所以此时，**只能**采用 `tail` 回溯的办法，找到第一个( 不是**最新**找到的，而是**最前序的** )可用的线程。
        *   再但是，为什么取消的 `node.next.next` 指向的是 `node.next` 自己呢？在 `#cancelAcquire(Node node)` 的末尾，`node.next = node;` 代码块，取消的 `node` 节点，将其 `next` 指向了自己。
    *   最后，调用 `LockSupport的unpark(Thread thread)` 方法，唤醒该线程。详细的实现，在 [「3\. LockSupport」](#) 中。
        

3\. LockSupport
===============

从上面我可以看到，当需要阻塞或者唤醒一个线程的时候，AQS 都是使用 LockSupport 这个工具类来完成的。

> LockSupport 是用来创建锁和其他同步类的基本线程阻塞原语。

每个使用 LockSupport 的线程都会与一个许可与之关联：

*   如果该许可可用，并且可在进程中使用，则调用 `#park(...)` 将会立即返回，否则可能阻塞。
*   如果许可尚不可用，则可以调用 `#unpark(...)` 使其可用。
*   但是，注意许可**不可重入**，也就是说只能调用一次 `park(...)` 方法，否则会一直阻塞。

LockSupport 定义了一系列以 `park` 开头的方法来阻塞当前线程，`unpark(Thread thread)` 方法来唤醒一个被阻塞的线程。如下图所示：

![方法](https://gitee.com/chenssy/blog-home/raw/master/image/sijava/2018120812001.png)

*   `park(Object blocker)` 方法的blocker参数，主要是用来标识当前线程在等待的对象，该对象主要用于**问题排查和系统监控**。
*   park 方法和 `unpark(Thread thread)` 方法，都是**成对出现**的。同时 `unpark(Thread thread)` 方法，必须要在 park 方法执行之后执行。当然，并不是说没有调用 `unpark(Thread thread)` 方法的线程就会一直阻塞，park 有一个方法，它是带了时间戳的 `#parkNanos(long nanos)` 方法：为了线程调度禁用当前线程，最多等待指定的等待时间，除非许可可用。

3.1 park
--------

public static void park() {  
 UNSAFE.park(false, 0L);  
}  

3.2 unpark
----------

public static void unpark(Thread thread) {  
 if (thread != null)  
 UNSAFE.unpark(thread);  
}  

3.3 实现原理
--------

从上面可以看出，其内部的实现都是通过 `sun.misc.Unsafe` 来实现的，其定义如下：

// UNSAFE.java  
public native void park(boolean var1, long var2);  
public native void unpark(Object var1);  

两个都是 `native` 本地方法。Unsafe 是一个比较危险的类，主要是用于执行低级别、不安全的方法集合。尽管这个类和所有的方法都是公开的（使用 `public` 进行修饰），但是这个类的使用仍然受限，你无法在自己的 Java 程序中直接使用该类，因为只有授信的代码才能获得该类的实例。

> 老艿艿：因为本小节对 LockSupport 的分享略微有点简略，所以胖友需要自己看下 `java.util.concurrent.locks.LockSupport` ，例如 `#park(Object blocker)` 方法的实现。当然，这个类总体来说很简单，所以即使没有详细的源码解析，胖友也能很容易的理解。

参考资料
====

1.  方腾飞：《Java并发编程的艺术》的 [「5.2 队列同步器」](#) 和 [「5.5 LockSupport」](#) 章节。
2.  [《LockSupport 的 park 和 unpark 的基本使用，以及对线程中断的响应性》](https://blog.csdn.net/aitangyong/article/details/38373137?utm_source=tuicool&utm_medium=referral)

