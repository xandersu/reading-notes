     
[【死磕 Java 并发】—– J.U.C 之 AQS：同步状态的获取与释放](/JUC/sike/aqs-2/ "【死磕 Java 并发】—– J.U.C 之 AQS：同步状态的获取与释放")
=================================================================================================



在前面提到过，AQS 是构建 Java 同步组件的基础，我们期待它能够成为实现大部分同步需求的基础。

AQS 的设计模式采用的**模板方法模式**，子类通过继承的方式，实现它的抽象方法来管理同步状态。对于子类而言，它并没有太多的活要做，AQS 已经提供了大量的模板方法来实现同步，主要是分为三类：

*   独占式获取和释放同步状态
*   共享式获取和释放同步状态
*   查询同步队列中的等待线程情况。

自定义子类使用 AQS 提供的模板方法，就可以实现**自己的同步语义**。

1\. 独占式
=======

独占式，**同一时刻，仅有一个线程持有同步状态**。

1.1 独占式同步状态获取
-------------

> 老艿艿：[「1.1 独占式同步状态获取」](#) 整个小节，是本文最难的部分。请一定保持耐心。

`#acquire(int arg)` 方法，为 AQS 提供的**模板方法**。该方法为独占式获取同步状态，但是该方法对**中断不敏感**。也就是说，由于线程获取同步状态失败而加入到 CLH 同步队列中，后续对该线程进行中断操作时，线程**不会**从 CLH 同步队列中**移除**。代码如下：

1: public final void acquire(int arg) {  
2:     if (!tryAcquire(arg) &&  
3:         acquireQueued(addWaiter(Node.EXCLUSIVE), arg))  
4:         selfInterrupt();  
5: }  

*   第 2 行：调用 `#tryAcquire(int arg)` 方法，去尝试获取同步状态，获取成功则设置锁状态并返回 true ，否则获取失败，返回 false 。若获取成功，`#acquire(int arg)` 方法，直接返回，**不用线程阻塞**，自旋直到获得同步状态成功。
    
    *   `#tryAcquire(int arg)` 方法，**需要**自定义同步组件**自己实现**，该方法必须要保证**线程安全**的获取同步状态。代码如下：
        
        protected boolean tryAcquire(int arg) {  
         throw new UnsupportedOperationException();  
        }  
        
        *   **直接**抛出 UnsupportedOperationException 异常。
*   第 3 行：如果 `#tryAcquire(int arg)` 方法返回 false ，即获取同步状态失败，则调用 `#addWaiter(Node mode)` 方法，将当前线程加入到 CLH 同步队列尾部。并且， `mode` 方法参数为 `Node.EXCLUSIVE` ，表示**独占**模式。
    
*   第 3 行：调用 `boolean #acquireQueued(Node node, int arg)` 方法，自旋直到获得同步状态成功。详细解析，见 [「1.1.1 acquireQueued」](#) 中。另外，该方法的返回值类型为 `boolean` ，当返回 true 时，表示在这个过程中，发生过**线程中断**。但是呢，这个方法又会**清理**线程中断的**标识**，所以在种情况下，需要调用【第 4 行】的 `#selfInterrupt()` 方法，恢复线程中断的**标识**，代码如下：
    
    static void selfInterrupt() {  
     Thread.currentThread().interrupt();  
    }  
    

### 1.1.1 acquireQueued

`boolean #acquireQueued(Node node, int arg)` 方法，为一个**自旋**的过程，也就是说，当前线程（Node）进入同步队列后，就会进入一个自旋的过程，每个节点都会**自省**地观察，当条件满足，获取到同步状态后，就可以从这个自旋过程中退出，否则会一直执行下去。

**流程图**如下：

![流程图](https://gitee.com/chenssy/blog-home/raw/master/image/sijava/2018120811001.png)

**代码**如下：

 1: final boolean acquireQueued(final Node node, int arg) {  
 2:     // 记录是否获取同步状态成功  
 3:     boolean failed = true;  
 4:     try {  
 5:         // 记录过程中，是否发生线程中断  
 6:         boolean interrupted = false;  
 7:         /*  
 8:          * 自旋过程，其实就是一个死循环而已  
 9:          */  
10:         for (;;) {  
11:             // 当前线程的前驱节点  
12:             final Node p = node.predecessor();  
13:             // 当前线程的前驱节点是头结点，且同步状态成功  
14:             if (p == head && tryAcquire(arg)) {  
15:                 setHead(node);  
16:                 p.next = null; // help GC  
17:                 failed = false;  
18:                 return interrupted;  
19:             }  
20:             // 获取失败，线程等待--具体后面介绍  
21:             if (shouldParkAfterFailedAcquire(p, node) &&  
22:                     parkAndCheckInterrupt())  
23:                 interrupted = true;  
24:         }  
25:     } finally {  
26:         // 获取同步状态发生异常，取消获取。  
27:         if (failed)  
28:             cancelAcquire(node);  
29:     }  
30: }  

*   第 3 行：`failed` 变量，记录是否**获取同步状态**成功。
*   第 6 行：`interrupted` 变量，记录获取过程中，是否发生**线程中断**。
*   ========== 第 7 至 24 行：“死”循环，自旋直到获得同步状态成功。==========
*   第 12 行：调用 `Node#predecessor()` 方法，获得当前线程的**前一个**节点 `p` 。
*   第 14 行：`p == head` 代码块，若满足，则表示当前线程的**前一个**节点为**头**节点，因为 `head` 是**最后一个**获得同步状态成功的节点，此时调用 `#tryAcquire(int arg)` 方法，尝试获得同步状态。🙂 在 `#acquire(int arg)` 方法的【第 2 行】，也调用了这个方法。
*   第 15 至 18 行：当前节点( 线程 )获取同步状态**成功**：
    *   第 15 行：设置当前节点( 线程 )为**新**的 `head` 。
    *   第 16 行：设置**老**的**头**节点 `p` 不再指向下一个节点，让它自身更快的被 GC 。
    *   第 17 行：标记 `failed = false` ，表示**获取同步状态**成功。
    *   第 18 行：返回记录获取过程中，是否发生**线程中断**。
*   第 20 至 24 行：获取失败，线程等待**唤醒**，从而进行下一次的同步状态获取的尝试。详细解析，见 [《【死磕 Java 并发】—– J.U.C 之 AQS：阻塞和唤醒线程》](http://www.iocoder.cn/JUC/sike/aqs-3) 。详细解析，见 [「1.1.2 shouldParkAfterFailedAcquire」](#) 。
    *   第 21 行：调用 `#shouldParkAfterFailedAcquire(Node pre, Node node)` 方法，判断获取失败后，是否当前线程需要阻塞等待。
*   ========== 第 26 至 29 行：获取同步状态的过程中，**发生异常**，取消获取。==========
*   第 28 行：调用 `#cancelAcquire(Node node)` 方法，取消获取同步状态。详细解析，见 [「1.1.3 cancelAcquire」](#) 。

### 1.1.2 shouldParkAfterFailedAcquire

 1: private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {  
 2:     // 获得前一个节点的等待状态  
 3:     int ws = pred.waitStatus;  
 4:     if (ws == Node.SIGNAL) //  Node.SIGNAL  
 5:         /*  
 6:          * This node has already set status asking a release  
 7:          * to signal it, so it can safely park.  
 8:          */  
 9:         return true;  
10:     if (ws > 0) { // Node.CANCEL  
11:         /*  
12:          * Predecessor was cancelled. Skip over predecessors and  
13:          * indicate retry.  
14:          */  
15:         do {  
16:             node.prev = pred = pred.prev;  
17:         } while (pred.waitStatus > 0);  
18:         pred.next = node;  
19:     } else { // 0 或者 Node.PROPAGATE  
20:         /*  
21:          * waitStatus must be 0 or PROPAGATE.  Indicate that we  
22:          * need a signal, but don't park yet.  Caller will need to  
23:          * retry to make sure it cannot acquire before parking.  
24:          */  
25:         compareAndSetWaitStatus(pred, ws, Node.SIGNAL);  
26:     }  
27:     return false;  
28: }  

*   `pred` 和 `node` 方法参数，传入时，要求前者**必须是**后者的前一个节点。
*   第 3 行：获得前一个节点( `pre` )的等待状态。下面会根据这个状态有**三种**情况的处理。
*   第 4 至 9 行：等待状态为 `Node.SIGNAL` 时，表示 `pred` 的下一个节点 `node` 的线程**需要**阻塞**等待**。在 `pred` 的线程释放同步状态时，会对 `node` 的线程进行**唤醒**通知。所以，【第 9 行】返回 true ，表明当前线程可以被 **park**，**安全**的阻塞等待。
*   第 19 至 26 行：等待状态为 `0` 或者 `Node.PROPAGATE` 时，通过 **CAS** 设置，将状态修改为 `Node.SIGNAL` ，即下一次重新执行 `#shouldParkAfterFailedAcquire(Node pred, Node node)` 方法时，满足【第 4 至 9 行】的条件。
    *   但是，对于本次执行，【第 27 行】返回 false 。
    *   另外，等待状态不会为 `Node.CONDITION` ，因为它用在 ConditonObject 中。
*   第 10 至 18 行：等待状态为 `NODE.CANCELLED` 时，则表明该线程的前一个节点已经等待超时或者被中断了，则需要从 CLH 队列中将该前一个节点删除掉，循环回溯，直到前一个节点状态 `<= 0` 。
    *   对于本次执行，【第 27 行】返回 false ，需要下一次再重新执行 `#shouldParkAfterFailedAcquire(Node pred, Node node)` 方法，看看满足哪个条件。
    *   整个过程如下图：![过程](http://static.iocoder.cn/images/JUC/shouldParkAfterFailedAcquire-02.png)

### 1.1.3 cancelAcquire

 1: private void cancelAcquire(Node node) {  
 2:     // Ignore if node doesn't exist  
 3:     if (node == null)  
 4:         return;  
 5:   
 6:     node.thread = null;  
 7:   
 8:     // Skip cancelled predecessors  
 9:     Node pred = node.prev;  
10:     while (pred.waitStatus > 0)  
11:         node.prev = pred = pred.prev;  
12:   
13:     // predNext is the apparent node to unsplice. CASes below will  
14:     // fail if not, in which case, we lost race vs another cancel  
15:     // or signal, so no further action is necessary.  
16:     Node predNext = pred.next;  
17:   
18:     // Can use unconditional write instead of CAS here.  
19:     // After this atomic step, other Nodes can skip past us.  
20:     // Before, we are free of interference from other threads.  
21:     node.waitStatus = Node.CANCELLED;  
22:   
23:     // If we are the tail, remove ourselves.  
24:     if (node == tail && compareAndSetTail(node, pred)) {  
25:         compareAndSetNext(pred, predNext, null);  
26:     } else {  
27:         // If successor needs signal, try to set pred's next-link  
28:         // so it will get one. Otherwise wake it up to propagate.  
29:         int ws;  
30:         if (pred != head &&  
31:             ((ws = pred.waitStatus) == Node.SIGNAL ||  
32:              (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&  
33:             pred.thread != null) {  
34:             Node next = node.next;  
35:             if (next != null && next.waitStatus <= 0)  
36:                 compareAndSetNext(pred, predNext, next);  
37:         } else {  
38:             unparkSuccessor(node);  
39:         }  
40:   
41:         node.next = node; // help GC  
42:     }  
43: }  

*   第 2 至 4 行：忽略，若传入参数 `node` 为空。
*   第 6 行：将节点的等待线程置**空**。
*   第 9 行：获得 `node` 节点的**前**一个节点 `pred` 。
    *   第 10 至 11 行： 逻辑同 `#shouldParkAfterFailedAcquire(Node pred, Node node)` 的【第 15 至 17 行】。
*   第 16 行：获得 `pred` 的**下**一个节点 `predNext` 。在这个变量上，有很“复杂”的英文，我们来理解下：`predNext` 从表面上看，和 `node` 是等价的。
    *   但是实际上，存在多线程并发的情况，所以在【第 25 行】或者【第 36 行】中，我们调用 `#compareAndSetNext(...)` 方法，使用 **CAS** 的方式，设置 `pred` 的**下**一个节点。
    *   如果设置失败，说明当前线程和其它线程竞争**失败**，不需要做其它逻辑，因为 `pred` 的**下**一个节点已经被其它线程设置**成功**。
*   第 21 行：设置 `node` 节点的为**取消**的等待状态 `Node.CANCELLED` 。在这个变量上，有很“复杂”的英文，我们再来理解下：
    *   这里可以使用**直接写**，而不是 CAS 。
    *   在这个操作之后，其它 Node 节点可以忽略 `node` 。
    *   `Before, we are free of interference from other threads.` TODO 9000 芋艿，如何理解。
*   下面开始开始修改 `pred` 的**新**的下一个节点，一共分成**三**种情况。
*   ========== 第一种 ==========
*   第 24 行：如果 `node` 是**尾**节点，调用 `#compareAndSetTail(...)` 方法，**CAS** 设置 `pred` 为**新**的**尾**节点。
    *   第 25 行：若上述操作成功，调用 `#compareAndSetNext(...)` 方法，**CAS** 设置 `pred` 的**下**一个节点为空( `null` )。
*   ========== 第二种 ==========
*   第 30 行：`pred` 非**首**节点。
*   第 31 至 32 行：`pred` 的**等待状态**为 `Node.SIGNAL` ，或者可被 **CAS** 为 `Node.SIGNAL` 。
*   第 33 行：`pred` 的线程**非空**。
    *   TODO 9001 芋艿，如何理解。目前能想象到的，一开始 30 行为非头节点，在 33 的时候，结果成为头节点，线程已经为空了。
*   第 34 至 36 行：若 `node` 的 **下**一个节点 `next` 的**等待状态**非 `Node.CANCELLED` ，则调用 `#compareAndSetNext(...)` 方法，**CAS** 设置 `pred` 的**下**一个节点为 `next` 。
*   ========== 第三种 ==========
*   第 37 至 39 行：如果 `pred` 为**首**节点( 🙂 在【第 31 至 33 行】也会有别的情况 )，调用 `#unparkSuccessor(Node node)` 方法，唤醒 `node` 的**下**一个节点的线程等待。详细解析，见 [《【死磕 Java 并发】—– J.U.C 之 AQS：阻塞和唤醒线程》](http://www.iocoder.cn/JUC/sike/aqs-3) 。
    *   为什么此处需要唤醒呢？因为，`pred` 为**首**节点，`node` 的**下**一个节点的阻塞等待，需要 `node` 释放同步状态时进行唤醒。但是，`node` 取消获取同步状态，则不会再出现 `node` 释放同步状态时进行唤醒 `node` 的**下**一个节点。因此，需要此处进行唤醒。
*   ========== 第 二 \+ 三种 ==========
*   第 41 行：TODO 芋艿 9002 为啥是 next 为 node 。目前收集到的资料如下：
    *   http://donald-draper.iteye.com/blog/2360256
        
    *   `next` 的注释如下：
        
        /**  
         \* Link to the successor node that the current node/thread  
         \* unparks upon release. Assigned during enqueuing, adjusted  
         \* when bypassing cancelled predecessors, and nulled out (for  
         \* sake of GC) when dequeued.  The enq operation does not  
         \* assign next field of a predecessor until after attachment,  
         \* so seeing a null next field does not necessarily mean that  
         \* node is at end of queue. However, if a next field appears  
         \* to be null, we can scan prev's from the tail to  
         \* double-check.  The next field of cancelled nodes is set to  
         \* point to the node itself instead of null, to make life  
         \* easier for isOnSyncQueue.  
         */  
        
        *   最后一句话

1.2 独占式获取响应中断
-------------

AQS 提供了`acquire(int arg)` 方法，以供独占式获取同步状态，但是该方法**对中断不响应**，对线程进行中断操作后，该线程会依然位于CLH同步队列中，等待着获取同步状态。为了**响应中断**，AQS 提供了 `#acquireInterruptibly(int arg)` 方法。该方法在等待获取同步状态时，如果当前线程被中断了，会**立刻**响应中断，并抛出 InterruptedException 异常。

public final void acquireInterruptibly(int arg) throws InterruptedException {  
 if (Thread.interrupted())  
 throw new InterruptedException();  
 if (!tryAcquire(arg))  
 doAcquireInterruptibly(arg);  
}  

*   首先，校验该线程是否已经中断了，如果是，则抛出InterruptedException 异常。
*   然后，调用 `#tryAcquire(int arg)` 方法，尝试获取同步状态，如果获取成功，则直接返回。
*   最后，调用 `#doAcquireInterruptibly(int arg)` 方法，自旋直到获得同步状态成功，**或线程中断抛出 InterruptedException 异常**。
*   应该不仅仅 help gc

### 1.2.1 doAcquireInterruptibly

private void doAcquireInterruptibly(int arg)  
 throws InterruptedException {  
 final Node node = addWaiter(Node.EXCLUSIVE);  
 boolean failed = true;  
 try {  
 for (;;) {  
 final Node p = node.predecessor();  
 if (p == head && tryAcquire(arg)) {  
 setHead(node);  
 p.next = null; // help GC  
 failed = false;  
 return;  
 }  
 if (shouldParkAfterFailedAcquire(p, node) &&  
 parkAndCheckInterrupt())  
 throw new InterruptedException(); // <1>  
 }  
 } finally {  
 if (failed)  
 cancelAcquire(node);  
 }  
}  

它与 `#acquire(int arg)` 方法**仅有两个差别**：

1.  方法声明抛出 InterruptedException 异常。
2.  在中断方法处不再是使用 `interrupted` 标志，而是直接抛出 InterruptedException 异常，即 `<1>` 处。

1.3 独占式超时获取
-----------

AQS 除了提供上面两个方法外，还提供了一个增强版的方法 `#tryAcquireNanos(int arg, long nanos)` 。该方法为 `#acquireInterruptibly(int arg)` 方法的进一步增强，它除了响应中断外，还有**超时控制**。即如果当前线程没有在指定时间内获取同步状态，则会返回 false ，否则返回 true 。

**流程图**如下：

![流程图](https://gitee.com/chenssy/blog-home/raw/master/image/sijava/2018120811002.png)

**代码**如下：

public final boolean tryAcquireNanos(int arg, long nanosTimeout)  
 throws InterruptedException {  
 if (Thread.interrupted())  
 throw new InterruptedException();  
 return tryAcquire(arg) ||  
 doAcquireNanos(arg, nanosTimeout);  
}  

*   首先，校验该线程是否已经中断了，如果是，则抛出InterruptedException 异常。
*   然后，调用 `#tryAcquire(int arg)` 方法，尝试获取同步状态，如果获取成功，则直接返回。
*   最后，调用 `#tryAcquireNanos(int arg)` 方法，自旋直到获得同步状态成功，或线程中断抛出 InterruptedException 异常，**或超过指定时间返回获取同步状态失败**。

### 1.3.1 tryAcquireNanos

static final long spinForTimeoutThreshold = 1000L;  
  
 1: private boolean doAcquireNanos(int arg, long nanosTimeout)  
 2:         throws InterruptedException {  
 3:     // nanosTimeout <= 0  
 4:     if (nanosTimeout <= 0L)  
 5:         return false;  
 6:     // 超时时间  
 7:     final long deadline = System.nanoTime() + nanosTimeout;  
 8:     // 新增 Node 节点  
 9:     final Node node = addWaiter(Node.EXCLUSIVE);  
 10:     boolean failed = true;  
 11:     try {  
 12:         // 自旋  
 13:         for (;;) {  
 14:             final Node p = node.predecessor();  
 15:             // 获取同步状态成功  
 16:             if (p == head && tryAcquire(arg)) {  
 17:                 setHead(node);  
 18:                 p.next = null; // help GC  
 19:                 failed = false;  
 20:                 return true;  
 21:             }  
 22:             /*  
 23:              * 获取失败，做超时、中断判断  
 24:              */  
 25:             // 重新计算需要休眠的时间  
 26:             nanosTimeout = deadline - System.nanoTime();  
 27:             // 已经超时，返回false  
 28:             if (nanosTimeout <= 0L)  
 29:                 return false;  
 30:             // 如果没有超时，则等待nanosTimeout纳秒  
 31:             // 注：该线程会直接从LockSupport.parkNanos中返回，  
 32:             // LockSupport 为 J.U.C 提供的一个阻塞和唤醒的工具类，后面做详细介绍  
 33:             if (shouldParkAfterFailedAcquire(p, node) &&  
 34:                     nanosTimeout > spinForTimeoutThreshold)  
 35:                 LockSupport.parkNanos(this, nanosTimeout);  
 36:             // 线程是否已经中断了  
 37:             if (Thread.interrupted())  
 38:                 throw new InterruptedException();  
 39:         }  
 40:     } finally {  
 41:         if (failed)  
 42:             cancelAcquire(node);  
 43:     }  
 44: }  

*   因为是在 `#doAcquireInterruptibly(int arg)` 方法的基础上，做了**超时**控制的增强，所以**相同部分，我们直接跳过**。
*   第 3 至 5 行：如果超时时间小于 0 ，直接返回 false ，已经超时。
*   第 7 行：计算最终超时时间 `deadline` 。
*   第 9 行：【相同，跳过】
*   第 10 行：【相同，跳过】
*   第 13 行：【相同，跳过】
*   第 14 行：【相同，跳过】
*   第 15 至 21 行：【相同，跳过】
*   第 26 行：重新计算**剩余**可获取同步状态的时间 `nanosTimeout` 。
*   第 27 至 29 行：如果剩余时间小于 0 ，直接返回 false ，已经超时。
*   第 33 行：【相同，跳过】
*   第 34 至 35 行：如果剩余时间大于 `spinForTimeoutThreshold` ，则调用 `LockSupport#parkNanos(Object blocker, long nanos)` 方法，休眠 `nanosTimeout` **纳**秒。否则，就不需要休眠了，直接进入**快速自旋**的过程。原因在于，`spinForTimeoutThreshold` 已经非常小了，**非常短的时间等待无法做到十分精确**，如果这时再次进行超时等待，相反会让 `nanosTimeout` 的超时从整体上面表现得不是那么精确。**所以，在超时非常短的场景中，AQS 会进行无条件的快速自旋**。
*   第 36 至 39 行：若线程已经中断了，抛出 InterruptedException 异常。
*   第 40 至 43 行：【相同，跳过】

1.4 独占式同步状态释放
-------------

当线程获取同步状态后，执行完相应逻辑后，就需要**释放同步状态**。AQS 提供了`#release(int arg)`方法，释放同步状态。代码如下：

1: public final boolean release(int arg) {  
2:     if (tryRelease(arg)) {  
3:         Node h = head;  
4:         if (h != null && h.waitStatus != 0)  
5:             unparkSuccessor(h);  
6:         return true;  
7:     }  
8:     return false;  
9: }  

*   第 2 行：调用 `#tryRelease(int arg)` 方法，去尝试释放同步状态，释放成功则设置锁状态并返回 true ，否则获取失败，返回 false 。同时，它们分别对应【第 3 至 6】和【第 8 行】的逻辑。
    
    *   `#tryRelease(int arg)` 方法，**需要**自定义同步组件**自己实现**，该方法必须要保证**线程安全**的释放同步状态。代码如下：
        
        protected boolean tryRelease(int arg) {  
         throw new UnsupportedOperationException();  
        }  
        
        *   **直接**抛出 UnsupportedOperationException 异常。
*   第 3 行：获得**当前**的 `head` ，避免并发问题。
    
*   第 4 行：头结点不为空，并且头结点状态不为 0 ( `INITAL` 未初始化)。为什么会出现 0 的情况呢？老艿艿的想法是，以 ReentrantReadWriteLock ( 😈 内部基于 AQS 实现 ) 举例子：
    
    *   线程 A 和线程 B ，都获取了读锁。
        
    *   线程 A 和线程 B ，基本同时释放锁，那么此时【第 3 行】`h` 可能获取到的是**相同**的 `head` 节点。此时，A 线程恰好先执行了 `#unparkSuccessor(Node node)` 方法，会将 `waitStatus` 设置为 0 。因此，`#unparkSuccessor(Node node)` 方法，对于 B 线程就不需要调用了。当然，更极端的情况下，可能 A 和 B 线程，都调用了 `#unparkSuccessor(Node node)` 方法。
        
        > 老艿艿：如上是我的猜想，并未实际验证。如果不正确，或者有其他情况，欢迎斧正。
        
*   第 5 行：调用 `#unparkSuccessor(Node node)` 方法，唤醒下一个节点的线程等待。详细解析，见 [《【死磕 Java 并发】—– J.U.C 之 AQS：阻塞和唤醒线程》](http://www.iocoder.cn/JUC/sike/aqs-3) 。
    

1.5 总结
------

这里稍微总结下：

> 在 AQS 中维护着一个 FIFO 的同步队列。
> 
> *   当线程获取同步状态失败后，则会加入到这个 CLH 同步队列的对尾，并一直保持着自旋。
> *   在 CLH 同步队列中的线程在自旋时，会判断其前驱节点是否为首节点，如果为首节点则不断尝试获取同步状态，获取成功则退出CLH同步队列。
> *   当线程执行完逻辑后，会释放同步状态，释放后会唤醒其后继节点。

2\. 共享式
=======

共享式与独占式的最主要区别在于，**同一时刻**：

*   独占式只能有**一个**线程获取同步状态。
*   共享式可以有**多个**线程获取同步状态。

例如，读操作可以有多个线程同时进行，而写操作同一时刻只能有一个线程进行写操作，其他操作都会被阻塞。参见 ReentrantReadWriteLock 。

2.1 共享式同步状态获取
-------------

AQS 提供 `#acquireShared(int arg)` 方法，共享式获取同步状态。代码如下：

> `#acquireShared(int arg)` 方法，对标 `#acquire(int arg)` 方法。

1: public final void acquireShared(int arg) {  
2:     if (tryAcquireShared(arg) < 0)  
3:         doAcquireShared(arg);  
4: }  

*   第 2 行：调用 `#tryAcquireShared(int arg)` 方法，尝试获取同步状态，获取成功则设置锁状态并返回大于等于 0 ，否则获取失败，返回小于 0 。若获取成功，直接返回，**不用线程阻塞**，自旋直到获得同步状态成功。
    *   `#tryAcquireShared(int arg)` 方法，**需要**自定义同步组件**自己实现**，该方法必须要保证**线程安全**的获取同步状态。代码如下：
        
        protected int tryAcquireShared(int arg) {  
         throw new UnsupportedOperationException();  
        }  
        
        *   **直接**抛出 UnsupportedOperationException 异常。

### 2.1.1 doAcquireShared

 1: private void doAcquireShared(int arg) {  
 2:     // 共享式节点  
 3:     final Node node = addWaiter(Node.SHARED);  
 4:     boolean failed = true;  
 5:     try {  
 6:         boolean interrupted = false;  
 7:         for (;;) {  
 8:             // 前驱节点  
 9:             final Node p = node.predecessor();  
10:             // 如果其前驱节点，获取同步状态  
11:             if (p == head) {  
12:                 // 尝试获取同步  
13:                 int r = tryAcquireShared(arg);  
14:                 if (r >= 0) {  
15:                     setHeadAndPropagate(node, r);  
16:                     p.next = null; // help GC  
17:                     if (interrupted)  
18:                         selfInterrupt();  
19:                     failed = false;  
20:                     return;  
21:                 }  
22:             }  
23:             if (shouldParkAfterFailedAcquire(p, node) &&  
24:                     parkAndCheckInterrupt())  
25:                 interrupted = true;  
26:         }  
27:     } finally {  
28:         if (failed)  
29:             cancelAcquire(node);  
30:     }  
31: }  

*   因为和 `#acquireQueued(int arg)` 方法的基础上，所以**相同部分，我们直接跳过**。
*   第 3 行：调用 `#addWaiter(Node mode)` 方法，将当前线程加入到 CLH 同步队列尾部。并且， `mode` 方法参数为 `Node.SHARED` ，表示**共享**模式。
*   第 6 行：【相同，跳过】
*   第 9 至 22 行：【大体相同，部分跳过】
    *   第 13 行：调用 `#tryAcquireShared(int arg)` 方法，尝试获得同步状态。🙂 在 `#acquireShared(int arg)` 方法的【第 2 行】，也调用了这个方法。
    *   第 15 行：调用 `#setHeadAndPropagate(Node node, int propagate)` 方法，设置**新**的首节点，并**根据条件**，唤醒下一个节点。详细解析，见 [「2.1.2 setHeadAndPropagate」](#) 。
        *   这里和**独占式**同步状态获取很大的不同：通过这样的方式，不断唤醒下一个**共享式**同步状态， 从而实现同步状态被**多个**线程的**共享获取**。
    *   第 17 至 18 行：和 `#acquire(int arg)` 方法，对于**线程中断**的处理方式相同，只是代码放置的位置不同。
*   第 23 至 25 行：【相同，跳过】
*   第 27 至 30 行：【相同，跳过】

### 2.1.2 setHeadAndPropagate

 1: private void setHeadAndPropagate(Node node, int propagate) {  
 2:     Node h = head; // Record old head for check below  
 3:     setHead(node);  
 4:     /*  
 5:      * Try to signal next queued node if:  
 6:      *   Propagation was indicated by caller,  
 7:      *     or was recorded (as h.waitStatus either before  
 8:      *     or after setHead) by a previous operation  
 9:      *     (note: this uses sign-check of waitStatus because  
10:      *      PROPAGATE status may transition to SIGNAL.)  
11:      * and  
12:      *   The next node is waiting in shared mode,  
13:      *     or we don't know, because it appears null  
14:      *  
15:      * The conservatism in both of these checks may cause  
16:      * unnecessary wake-ups, but only when there are multiple  
17:      * racing acquires/releases, so most need signals now or soon  
18:      * anyway.  
19:      */  
20:     if (propagate > 0 || h == null || h.waitStatus < 0 ||  
21:         (h = head) == null || h.waitStatus < 0) {  
22:         Node s = node.next;  
23:         if (s == null || s.isShared())  
24:             doReleaseShared();  
25:     }  
26: }  

*   第 2 行：记录**原来**的**首**节点 `h` 。
*   第 3 行：调用 `#setHead(Node node)` 方法，设置 `node` 为**新**的**首**节点。
*   第 20 行：`propagate > 0` 代码块，说明同步状态还能被其他线程获取。
*   第 20 至 21 行：判断**原来**的或者**新**的**首**节点，**等待状态**为 `Node.PROPAGATE` 或者 `Node.SIGNAL` 时，可以继续向下**唤醒**。
*   第 23 行：调用 `Node#isShared()` 方法，判断**下**一个节点为**共享式**获取同步状态。
*   第 24 行：调用 `#doReleaseShared()` 方法，唤醒后续的**共享式**获取同步状态的节点。详细解析，见 [「2.1.2 setHeadAndPropagate」](#) 。

2.2 共享式获取响应中断
-------------

`#acquireSharedInterruptibly(int arg)` 方法，代码如下：

public final void acquireSharedInterruptibly(int arg)  
 throws InterruptedException {  
 if (Thread.interrupted())  
 throw new InterruptedException();  
 if (tryAcquireShared(arg) < 0)  
 doAcquireSharedInterruptibly(arg);  
}  
  
private void doAcquireSharedInterruptibly(int arg)  
 throws InterruptedException {  
 final Node node = addWaiter(Node.SHARED);  
 boolean failed = true;  
 try {  
 for (;;) {  
 final Node p = node.predecessor();  
 if (p == head) {  
 int r = tryAcquireShared(arg);  
 if (r >= 0) {  
 setHeadAndPropagate(node, r);  
 p.next = null; // help GC  
 failed = false;  
 return;  
 }  
 }  
 if (shouldParkAfterFailedAcquire(p, node) &&  
 parkAndCheckInterrupt())  
 throw new InterruptedException();  
 }  
 } finally {  
 if (failed)  
 cancelAcquire(node);  
 }  
}  

*   与 [「1.2 独占式获取响应中断」](#) 类似，就不重复解析了。

2.3 共享式超时获取
-----------

`#tryAcquireSharedNanos(int arg, long nanosTimeout)` 方法，代码如下：

public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)  
 throws InterruptedException {  
 if (Thread.interrupted())  
 throw new InterruptedException();  
 return tryAcquireShared(arg) >= 0 ||  
 doAcquireSharedNanos(arg, nanosTimeout);  
}  
  
private boolean doAcquireSharedNanos(int arg, long nanosTimeout)  
 throws InterruptedException {  
 if (nanosTimeout <= 0L)  
 return false;  
 final long deadline = System.nanoTime() + nanosTimeout;  
 final Node node = addWaiter(Node.SHARED);  
 boolean failed = true;  
 try {  
 for (;;) {  
 final Node p = node.predecessor();  
 if (p == head) {  
 int r = tryAcquireShared(arg);  
 if (r >= 0) {  
 setHeadAndPropagate(node, r);  
 p.next = null; // help GC  
 failed = false;  
 return true;  
 }  
 }  
 nanosTimeout = deadline - System.nanoTime();  
 if (nanosTimeout <= 0L)  
 return false;  
 if (shouldParkAfterFailedAcquire(p, node) &&  
 nanosTimeout > spinForTimeoutThreshold)  
 LockSupport.parkNanos(this, nanosTimeout);  
 if (Thread.interrupted())  
 throw new InterruptedException();  
 }  
 } finally {  
 if (failed)  
 cancelAcquire(node);  
 }  
}  

*   与 [「1.3 独占式超时获取」](#) 类似，就不重复解析了。

2.4 共享式同步状态释放
-------------

当线程获取同步状态后，执行完相应逻辑后，就需要**释放同步状态**。AQS 提供了`#releaseShared(int arg)`方法，释放同步状态。代码如下：

1: public final boolean releaseShared(int arg) {  
2:     if (tryReleaseShared(arg)) {  
3:         doReleaseShared();  
4:         return true;  
5:     }  
6:     return false;  
7: }  

*   第 2 行：调用 `#tryReleaseShared(int arg)` 方法，去尝试释放同步状态，释放成功则设置锁状态并返回 true ，否则获取失败，返回 false 。同时，它们分别对应【第 3 至 5】和【第 6 行】的逻辑。
    
    *   `#tryReleaseShared(int arg)` 方法，**需要**自定义同步组件**自己实现**，该方法必须要保证**线程安全**的释放同步状态。代码如下：
        
        protected boolean tryReleaseShared(int arg) {  
         throw new UnsupportedOperationException();  
        }  
        
        *   **直接**抛出 UnsupportedOperationException 异常。
*   第 3 行：调用 `#doReleaseShared()` 方法，唤醒后续的**共享式**获取同步状态的节点。
    

### 2.4.1 doReleaseShared

 1: private void doReleaseShared() {  
 2:     /*  
 3:      * Ensure that a release propagates, even if there are other  
 4:      * in-progress acquires/releases.  This proceeds in the usual  
 5:      * way of trying to unparkSuccessor of head if it needs  
 6:      * signal. But if it does not, status is set to PROPAGATE to  
 7:      * ensure that upon release, propagation continues.  
 8:      * Additionally, we must loop in case a new node is added  
 9:      * while we are doing this. Also, unlike other uses of  
10:      * unparkSuccessor, we need to know if CAS to reset status  
11:      * fails, if so rechecking.  
12:      */  
13:     for (;;) {  
14:         Node h = head;  
15:         if (h != null && h != tail) {  
16:             int ws = h.waitStatus;  
17:             if (ws == Node.SIGNAL) {  
18:                 if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))  
19:                     continue;            // loop to recheck cases  
20:                 unparkSuccessor(h);  
21:             }  
22:             else if (ws == 0 &&  
23:                      !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))  
24:                 continue;                // loop on failed CAS  
25:         }  
26:         if (h == head)                   // loop if head changed  
27:             break;  
28:     }  
29: }  

*   TODO 9003 doReleaseShared 的详细逻辑。可参考博客：http://zhanjindong.com/2015/03/15/java-concurrent-package-aqs-AbstractQueuedSynchronizer

参考资料
====

*   Doug Lea：《Java并发编程实战》
*   方腾飞：《Java并发编程的艺术》的 [「5.2 队列同步器」](#) 章节。
*   [《Java 并发包源码学习之 AQS 框架（四）AbstractQueuedSynchronizer 源码分析》](http://zhanjindong.com/2015/03/15/java-concurrent-package-aqs-AbstractQueuedSynchronizer)
*   [《一行一行源码分析清楚 AbstractQueuedSynchronizer》](https://javadoop.com/post/AbstractQueuedSynchronizer#)

666\. 彩蛋
========

