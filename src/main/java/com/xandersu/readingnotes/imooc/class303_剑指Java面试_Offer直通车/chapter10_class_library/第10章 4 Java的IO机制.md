# 第10章 4 Java的IO机制



## BIO、NIO、AIO

### Block-IO:InputStream和OutputStream，Reader和Writer

同步阻塞

1. 系统调用
   - 无数据
2. 等待数据返回
   - 数据准备好
3. 数据从内核复制到用户空间
   - 数据拷贝完成
4. 程序可以使用数据



### NonBlock-IO：构建多路复用的、同步非阻塞的IO操作

程序调用后，会不断轮询数据的状态

- channels：
  - 数据从channels读到buffers，也可以从buffers写到channels
  - FileChannel
    - transferTo : 把FileChannel中的数据拷贝到另外一个channel
    - transFrom : 把另外一个channel中的数据拷贝到FileChannel
    - 避免了两次用户态和内核态见的上下文切换，即“零拷贝”，效率高
  - DataGramChannel
  - SocketChannel
  - ServerSocketChannel
- buffers
  - ByteBuffer
  - CharBuffer
  - DoubleBuffer
  - FloatBuffer
  - IntBuffer
  - LongBuffer
  - ShortBuffer
  - MappedByteBuffer
- selectors
  - 允许单线程处理多个channel
  - 注册selector



### IO多路复用：调用系统级别的select\poll\epoll

#### 支持一个线程所能打开的最大连接数

|        |                                                              |
| ------ | ------------------------------------------------------------ |
| select | 单个进程所能打开的最大连接数由FD_SETSIZE宏定义，其大小是32个整数的大小（32位的机器上，大小是32\*32，64位机器上FD_SETSIZE为32\*64）,我们可以对其进行修改，然后重新编译内核，但是性能无法保证，需要做进一步测试。    连接数是有限的，原因是底层是数组 |
| poll   | 本质上和select没有区别，但是他没有最大连接数的限制，原因是它基于链表来存储的 |
| epoll  | 虽然连接数有上限，但是很大，1G内存的机器上可以打开10万左右的连接 |

#### FD剧增后带来的IO效率问题

|        |                                                              |
| ------ | ------------------------------------------------------------ |
| select | 因为每次调用时都会对连接进行线性遍历，所以随着FD的增加会造成遍历速度的“线性下降“的性能问题 |
| poll   | 本质上和select没有区别，但是他没有最大连接数的限制，原因是它基于链表来存储的 |
| epoll  | 虽然连接数有上限，但是很大，1G内存的机器上可以打开10万左右的连接 |

#### 消息的传递方式

|        |                                                              |
| ------ | ------------------------------------------------------------ |
| select | 因为每次调用时都会对连接进行线性遍历，所以随着FD的增加会造成遍历速度的“线性下降“的性能问题 |
| poll   | 同上                                                         |
| epoll  | 通过内核和用户空间共享一块内存来实现，性能较高               |



### AIO：基于事件和回调机制

### AIO如何进一步加工处理结果

- 基于回调：实现CompletionHandler接口，调用时触发回调函数
- 返回Future：通过isDone()查看是否已经准备好，通过get()等待返回数据



BIO、NIO、AIO对比

| 属性\模型                | 阻塞BIO    | 非阻塞NIO    | 异步AIO        |
| ------------------------ | ---------- | ------------ | -------------- |
| blocking                 | 阻塞并同步 | 非阻塞但同步 | 非阻塞并且异步 |
| 线程数（server：client） | 1：1       | 1：N         | 0：N           |
| 复杂度                   | 简单       | 较复杂       | 复杂           |
| 吞吐量                   | 低         | 高           | 高             |







