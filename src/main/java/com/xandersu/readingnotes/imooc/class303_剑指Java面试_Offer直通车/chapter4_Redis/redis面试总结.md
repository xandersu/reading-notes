stop-writes-on-bgsave-error yes //表示当备份进程出错时，主进程不接受写入操作，如果业务有完善的监控系统，可以关闭。

rdbcompression yes //把rdb文件压缩后保存，建议关闭,CPU密集型，开启增加cpu压力

为什么Redis这么快

100000+ QPS(query per second 每秒内查询次数)

完全基于内存，绝大部分请求时纯粹的内存操作，执行效率高。Redis采用单进程单线程的K-V数据库，由C语言编写，数据存储于内存中，读写数据不受硬盘IO限制。

数据结构简单，对数据操作也简单。Redis不使用表，不会预 定义或者强制要求用户对redis存储的不同的数据进行关联，性能比关系型数据库高很多，存储结构是键值对类似于hashmap，查找和操作时时间复杂度是O(1)。

采用单线程，单线程也能处理高并发请求，想多核也可以启动多实例。面对高并发情况，首先使用单线程来处理，将IO线程与业务线程分开，业务线程使用线程池来避免频繁创建和销毁线程，即便是一次请求阻塞了也不会影响其他请求。Redis的单线程结构是主线程是单线程的。  主线程包括IO事件处理，IO对应的相关业务请求的处理；主线程负责过期键处理，复制协调和集群协调等，这些除了IO时间的逻辑会被封装成周期性的任务，由主线程周期性的处理，正因为采用单线程的设计，对于客户端的所有读写请求都有一个主线程串行的处理，因此多个客户端对一个键进行写操作的时候要不会有并发的问题，避免了频繁的上下文切换和锁竞争，效率更高。单线程可以处理高并发的请求，并发不是并行，Redis单线程等配合IO多路复用大幅提高性能。处理网络请求时是单线程，一个Redis server不是单线程的。

使用多路I/O复用模型，非阻塞IO。 所有操作都是按照顺序线性执行，但是由于读写操作，等待用户输入、输出是阻塞的，所以IO操作不能直接返回。IO多路复用解决问题。

多路I/O复用

FD：file Descriptior，文件描述符

一个打开的文件通过唯一的描述符进行引用，该描述符是打开文件的元数据到文件本身的映射。

传统的阻塞I/O模型

多路I/O复用

Select系统调用

thread
|
selector
| | |
channel |channel | channel

Redis采用的I/O多路复用函数：epoll/kqueue/evport/select。

因地制宜，根据不同平台使用不同IO多路复用函数，作为子模块提供给上层统一的接口。

优先选择时间复杂度O(1)的IO多路复用函数作为底层实现。evport,epoll,kqueue

时间复杂度为O(n)的select作为保底

基于react设计模式监听IO事件。文件事件处理器

redis命令是原子性的。

string:最基本的数据类型，二进制安全。KV键值对，值最大存储512M。redis string可以包含任何数据，例jpg图片，或者序列化的对象。

 set name "redis";
 set count 1;
 get count;
 incr count;

简单动态字符串

  /**
   * 保存字符串对象的结构
   */
  struct sdshdr{
  //buf中已占用空间的长度
  int len;
  //buf 中剩余可用空间的长度
  int free;
  //数据空间
  char buf[];
  }


Hash:string元素组成的字典，适合用于存储对象 
hmset lilei namr "lilei" age 26 title "XXX"
hget lilei age
hget lilei title
hset lilei title "yyyy"

 List:列表，按照String元素插入顺序排序，可以添加一个元素到头部或者尾部。栈后进先出
lpush mylist aaa
lrange mylist 0 10 //拿出0-10

Set:String 元素组成的无序集合，通过hash表实现，不允许重复。
redis为set提供求交集、并集、差集操作。
sadd myset 111 //添加不重复元素返回1
sadd myset 111 //添加重复元素返回0
smembers myset //查看元素

Sorted Set: 通过分数（score,double类型）来为集合中的成员进行从小到大的排序。
zadd myzset 3 abc
zadd myzset 1 abb
zadd myzset 2 abb //重复元素返回0
zrangebyscore myzset 0 10 

用于计数的HyperLogLog，用于支持存储地理位置信息的Geo

底层数据类型基础

1. 简单动态字符串
2. 链表
3. 字典
4. 跳跃表
5. 整数集合
6. 压缩列表
7. 对象

从海量数据里查询某一固定前缀的key

KEYS pattern:查找所有符合给定模式pattern的key

dbsize key个数

keys k1* 查找所有k1开头的


- keys指令一次性返回所有匹配的key
- 键的数量过大会使服务器卡顿

SCAN cursor [match pattern] [count count]

- 基于游标的迭代器，需要基于上一次的游标延续之前的迭代过程
- 以0作为游标开始一次新的迭代，直到命令返回游标0完成一次遍历
- 不保证每次执行都返回某个给定数量的元素，支持模糊查询。可能返回0，但返回游标不是0，应用程序就应该继续遍历，不能视为结束。
- 一次返回的数量不可控，只能大概率符合count参数。

clear 清屏

scan 0 match k1* count 10  开始迭代（cursor传0）返回前缀为k1的key，期望每次返回10个
-> 1) "1153XXXX" 游标位置
-> 2) 1) "k1XXXX"
->    2) "k1XXXX"
->    3) "k1XXXX"

scan 1153XXXX match k1* count 10 


**cursor返回的不一定是递增，可能后面比前面小，可能会获取到重复key.业务要去重**

如何实现分布式锁

互斥性。任意时刻只能有有个客户端获取锁。

安全性。锁只能有获取到该锁的客户端删除，不能由其他的客户端删除。

死锁。

容错。部分节点down，客户端仍能获取锁和释放锁。

SETNX key value：如果key不存在，则创建并赋值

 时间复杂度：O(1)

 返回值：设置成功，返回1；设置失败，返回0.

get locknx -> nil 说明不存在该key
setnx locknx test -> 1 返回1说明成功；
setnx locknx task -> 0 返回0说明失败；
get locknx -> "test" 说明没有被后来的setnx更改

如何解决SETNX长期有效的问题 

EXPIRE key seconds

设置key生存时间，当key过期时（生存时间为0）,会被自动删除

缺点：原子性得不到满足

setnx locknx task -> 0 占用
expire locknx 2 -> 1 设置过期时间2秒

SET key value [EX seconds] [PX milliseconds] [NX|XX]  

 EX seconds:设置键的过期时间为second秒，秒级
 PX milliseconds:设置键的过期时间为millisecond毫秒，毫秒级 
 NX：只在键不存在时，才对键进行设置操作 
 XX:只在键存在时，才对键进行设置操作 
 SET操作成功完成时，返回ok，否则返回nil 

对key locktarget 赋予 12345的值，值可以为request id或者线程id，用来标识当前占用锁的是哪个线程或者那个请求,有效时间10秒，在key不存在时才设置

set locktarget 12345 ex 10 nx ;


大量key同时过期的注意事项

集中过期，由于清除大量的key很耗时，会出现短暂的卡顿现象

解决方案：在设置key过期时间时，给每个key加上随机值。

如何实现异步队列

使用List作为队列，RPUSH生产消息，LPOP消费消息

rpush testlist aaa;
rpush testlist bbb;
rpush testlist ccc;

lpop testlist -> aaa
lpop testlist -> bbb
lpop testlist -> ccc

 缺点：没有等待队列里有值就直接消费
 弥补：可以通过在应用层引入Sleep机制去调用LPOP重试 

BLPOP key [key ...] timeout：阻塞直到队列有消息或者超时

lpop testlist -> nil
blpop testline 30 
rpush testlist aaa

缺点：只能供一个消费者消费

pub/sub:主题订阅模式

发送者(pub)发送消息，订阅者(sub)接收消息

订阅者可以订阅任意数量的频道(topic)

pub
topic 
sub sub sub 
subscribe myTopic
subscribe anotherTopic
publish myTopic "hello"

消息的发布是无状态的，无法保证可达 

Redis如何做持久化

RDB（快照）持久化：保存某个时间点的全量数据快照

在特定间隔保存这个时间点的全部数据快照

缺点：内存数据的全量同步，数据量大会由于IO而严重影响性能

缺点：可能会因为Redis挂掉而丢失从当前至最近一次快照期间的数据

redis.conf
save 900 1 //900秒内如果有有1条写入就触发产生快照
save 300 10 //300秒内如果有有10条写入就触发产生快照
save 60 10000 //60秒内如果有有10000条写入就触发产生快照
save "" //禁用RDB配置

stop-writes-on-bgsave-error yes //表示当备份进程出错时，主进程不接受写入操作，如果业务有完善的监控系统，可以关闭。
rdbcompression yes //把rdb文件压缩后保存，建议关闭,CPU密集型，开启增加cpu压力

 
save：阻塞redis的服务器进程，直到RDB文件被创建完毕。（很少使用，在主线程操作，阻塞主线程）

BGSAVE:FORK出一个子进程来创建RDB文件，不阻塞服务器进程。

save //服务端卡顿
rm -f dump.rdb
ls dump.rdb
./redis-cli
last save //返回数字，上次执行save指令的时间
bgsave //客户端不卡顿
last save //返回数字，时间变化，上次执行save指令的时间
mv dump.rdb dumpxxxx.rdb //定期保存某个时间点的数据备份

自动触发RDB持久化方式 

根据redis.conf配置里save m n 定时触发(用的是bgsave)

主从复制，主节点自动触发

执行debug reload

执行shutdown且没有开启AOF持久化

BGSAVE原理

检查是否存在AOF/RDB的子进程正在进行，有就返回错误。防止子进程的竞争

触发持久化

调用rdbSaveBackground方法

执行fork

主线程相应其他操作

子进程执行rdb操作
 
系统调用fork():创建进程，实现了copy-on-write 

copy-on-write

如果有多个调用者同时要求相同资源（如内存或磁盘上的数据存储），他们会共同获取相同的指针指向相同的资源，知道某个调用者企图修改资源的内容时，系统才会真正复制一份专用副本给该调用者，而其他调用者所见到的最初的资源仍然保持不变

缺点

缺点：内存数据的全量同步，数据量大会由于IO而严重影响性能

缺点：可能会因为Redis挂掉而丢失从当前至最近一次快照期间的数据

AOF（append-only-file）持久化：保存写状态

rdb备份数据库状态，aof备份redis接收的指令

记录下除了查询以外的所有变更数据库状态的指令

以append的形式最佳保存到AOF文件中（增量）

AOF默认关闭的

vim redis.conf

/app
appendonly no -> appendonly yes
appendfilename "appendonly.aof"
//aof文件写入方式
appendfsync always //缓存区更改了就及时记录到aof中
appendfsync everysec //缓存区内容每隔一秒
appendfsync no //交给操作系统，一般是缓存区满了就写入磁盘

日志重写解决AOF文件大小不断增大的问题，原理如下：

调用fork()，创建子进程

子进程把新的AOF写到一个临时文件中，不依赖原来的AOF文件

主进程持续将新的变动同时写到内存和原来的AOF里

主进程获取子进程重写AOF的完成信号，往新AOF同步增量变动

使用新的AOF文件替换掉旧的AOF文件

RDB和AOF文件共存情况下的恢复流程

redis->存在AOF则加载AOF->结束

->不存在aof，如果存在RDB，则加载RDB->结束

->都不存在->直接结束

RDB和AOF的优缺点

RDB优点：全量数据快照，文件小，恢复快

RDB缺点：无法保存最近一次快照之后的数据

AOF优点：可读性高，适合保存增量数据，数据不易丢失

AOF缺点：文件体积大，恢复时间长

RDB-AOF混合持久化方式（默认）

BGSAVE作镜像全量持久化，AOF做增量持久化

Pipeline及主从同步

使用pipeline的好处

pipeline和linux的管道类似

Redis基于请求/相应模型，单个请求处理需一一应答

pipeline批量执行指令，节约多次IO往返的时间

有顺序依赖的指令建议分批发送

Redis的同步机制

主从同步原理

全同步流程

salve发送sync命令道master

master启动一个后台进程，将redis中的数据快照保存到文件中

master将保存数据快照期间接收到的写命令缓存起来

master完成写文件操作后，将该文件发送给salve

使用新的AOF文件替换掉旧的AOF文件

Master将这期间收集的增量写命令发送给salve端

增量同步过程

master接收到用户的操作命令，判断是否需要传播到slave

将操作记录追加到aof文件

将操作传播到其他slave，1、对齐主从库；2、往相应缓存写入指令

将缓存中的数据发送给slave

Redis Sentinel

解决主从同步Master宕机后的主从切换问题：

监控： 检查主从服务器是否运行正常

提醒： 通过API向管理员或者其他应用程序发送故障通知

自动故障迁移：主从切换

流言协议Gossip

在杂乱无章中寻求一致

每个节点都随机地与对方通信，最终所有节点的状态达成一致

种子节点定期随机向其他节点发送节点列表以及需要传播的消息

不保证信息一定会传递给所有节点，但是最终会趋于一致

Redis集群原理

如何从海量数据中快速找到所需

分片：按照某种规则去划分数据，分散存储在多个节点上

常规的按照哈希划分无法实现节点的动态增减

一致性哈希算法：对2^32取模，将哈希值空间组织成虚拟的圆环

Hash环的数据倾斜问题，引入虚拟节点解决数据倾斜的问题

Redis底层数据结构

Redis详解（四）------ redis的底层数据结构

https://www.cnblogs.com/ysocean/p/9080942.html
OBJECT ENCODING    key

用来显示那五大数据类型的底层数据结构。

简单动态字符串
struct sdshdr{
     //记录buf数组中已使用字节的数量
     //等于 SDS 保存字符串的长度
     int len;
     //记录 buf 数组中未使用字节的数量
     int free;
     //字节数组，用于保存字符串
     char buf[];
}

图片来源：《Redis设计与实现》
1、len 保存了SDS保存字符串的长度
2、buf[] 数组用来保存字符串的每个元素
3、free j记录了 buf 数组中未使用的字节数量
①、常数复杂度获取字符串长度
②、杜绝缓冲区溢出
在进行字符修改的时候，会首先根据记录的 len 属性检查内存空间是否满足需求，如果不满足，会进行相应的空间扩展，然后在进行修改操作，所以不会出现缓冲区溢出。
③、减少修改字符串的内存重新分配次数
SDS实现了空间预分配和惰性空间释放两种策略：
　　1、空间预分配：对字符串进行空间扩展的时候，扩展的内存比实际需要的多，这样可以减少连续执行字符串增长操作所需的内存重分配次数。
　　2、惰性空间释放：对字符串进行缩短操作时，程序不立即使用内存重新分配来回收缩短后多余的字节，而是使用 free 属性将这些字节的数量记录下来，等待后续使用。（当然SDS也提供了相应的API，当我们有需要时，也可以手动释放这些未使用的空间。）
④、二进制安全
C字符串以空字符作为字符串结束的标识，而对于一些二进制文件（如图片等），内容可能包括空字符串，因此C字符串无法正确存取；而所有 SDS 的API 都是以处理二进制的方式来处理 buf 里面的元素，并且 SDS 不是以空字符串来判断是否结束，而是以 len 属性表示的长度来判断字符串是否结束。
⑤、兼容部分 C 字符串函数
　　虽然 SDS 是二进制安全的，但是一样遵从每个字符串都是以空字符串结尾的惯例，这样可以重用 C 语言库<string.h> 中的一部分函数。

链表
链表定义：
typedef  struct listNode{
       //前置节点
       struct listNode *prev;
       //后置节点
       struct listNode *next;
       //节点的值
       void *value;  
}listNode
　Redis链表特性：
　　①、双端：链表具有前置节点和后置节点的引用，获取这两个节点时间复杂度都为O(1)。
　　②、无环：表头节点的 prev 指针和表尾节点的 next 指针都指向 NULL,对链表的访问都是以 NULL 结束。　　
　　③、带链表长度计数器：通过 len 属性获取链表长度的时间复杂度为 O(1)。
　　④、多态：链表节点使用 void* 指针来保存节点值，可以保存各种不同类型的值。
字典
符号表或者关联数组、或映射（map），是一种用于保存键值对的抽象数据结构。
哈希表结构定义：
typedef struct dictht{
     //哈希表数组
     dictEntry **table;
     //哈希表大小
     unsigned long size;
     //哈希表大小掩码，用于计算索引值
     //总是等于 size-1
     unsigned long sizemask;
     //该哈希表已有节点的数量
     unsigned long used;
}dictht
哈希表是由数组 table 组成，table 中每个元素都是指向 dict.h/dictEntry 结构，dictEntry 结构定义如下：
typedef struct dictEntry{
     //键
     void *key;
     //值
     union{
          void *val;
          uint64_tu64;
          int64_ts64;
     }v;
 
     //指向下一个哈希表节点，形成链表
     struct dictEntry *next;
}dictEntry
key 用来保存键，val 属性用来保存值，值可以是一个指针，也可以是uint64_t整数，也可以是int64_t整数。
使用链地址法，通过next这个指针可以将多个哈希值相同的键值对连接在一起，用来解决哈希冲突。
①、哈希算法
#1、使用字典设置的哈希函数，计算键 key 的哈希值
hash = dict->type->hashFunction(key);
#2、使用哈希表的sizemask属性和第一步得到的哈希值，计算索引值
index = hash & dict->ht[x].sizemask;

②、解决哈希冲突：链地址法。通过字典里面的 *next 指针指向下一个具有相同索引值的哈希表节点。
③、扩容和收缩：当哈希表保存的键值对太多或者太少时，就要通过 rerehash(重新散列）来对哈希表进行相应的扩展或者收缩。
1、如果执行扩展操作，会基于原哈希表创建一个大小等于 ht[0].used*2n 的哈希表（也就是每次扩展都是根据原哈希表已使用的空间扩大一倍创建另一个哈希表）。相反如果执行的是收缩操作，每次收缩是根据已使用空间缩小一倍创建一个新的哈希表。
2、重新利用上面的哈希算法，计算索引值，然后将键值对放到新的哈希表位置上。
3、所有键值对都迁徙完毕后，释放原哈希表的内存空间。
④、触发扩容的条件：
1、服务器目前没有执行 BGSAVE 命令或者 BGREWRITEAOF 命令，并且负载因子大于等于1。
2、服务器目前正在执行 BGSAVE 命令或者 BGREWRITEAOF 命令，并且负载因子大于等于5。
⑤、渐近式 rehash
扩容和收缩操作不是一次性、集中式完成的，而是分多次、渐进式完成的。
在进行渐进式rehash期间，字典的删除查找更新等操作可能会在两个哈希表上进行，第一个哈希表没有找到，就会去第二个哈希表上进行查找。但是进行 增加操作，一定是在新的哈希表上进行的。
跳跃表
跳跃表节点定义如下：
typedef struct zskiplistNode {
     //层
     struct zskiplistLevel{
           //前进指针
           struct zskiplistNode *forward;
           //跨度
           unsigned int span;
     }level[];
 
     //后退指针
     struct zskiplistNode *backward;
     //分值
     double score;
     //成员对象
     robj *obj;
} zskiplistNode
多个跳跃表节点构成一个跳跃表：
typedef struct zskiplist{
     //表头节点和表尾节点
     structz skiplistNode *header, *tail;
     //表中节点的数量
     unsigned long length;
     //表中层数最大的节点的层数
     int level;
}zskiplist;
①、搜索：从最高层的链表节点开始，如果比当前节点要大和比当前层的下一个节点要小，那么则往下找，也就是和当前层的下一层的节点的下一个节点进行比较，以此类推，一直找到最底层的最后一个节点，如果找到则返回，反之则返回空。
②、插入：首先确定插入的层数，有一种方法是假设抛一枚硬币，如果是正面就累加，直到遇见反面为止，最后记录正面的次数作为插入的层数。当确定插入的层数k后，则需要将新元素插入到从底层到k层。
③、删除：在各个层中找到包含指定值的节点，然后将节点从链表中删除即可，如果删除以后只剩下头尾两个节点，则删除这一层。
整数集合
数集合（intset）是Redis用于保存整数值的集合抽象数据类型，它可以保存类型为int16_t、int32_t 或者int64_t 的整数值，并且保证集合中不会出现重复元素。
typedef struct intset{
     //编码方式
     uint32_t encoding;
     //集合包含的元素数量
     uint32_t length;
     //保存元素的数组
     int8_t contents[];
 
}intset;
压缩列表ziplist
压缩列表的原理：压缩列表并不是对数据利用某种算法进行压缩，而是将数据按照一定规则编码在一块连续的内存区域，目的是节省内存。
Redis的同步机制 
https://blog.csdn.net/yan245294305/article/details/95305744#7.%E5%BF%AB%E8%A1%A8%EF%BC%88quicklist%EF%BC%89
Redis详解（八）------ 主从复制
https://www.cnblogs.com/ysocean/p/9143118.html
Redis系列八：redis主从复制和哨兵
https://www.cnblogs.com/leeSmall/p/8398401.html
主从复制原理
Redis的复制功能分为同步（sync）和命令传播（command propagate）两个操作。
①、旧版同步
　　当从节点发出 SLAVEOF 命令，要求从服务器复制主服务器时，从服务器通过向主服务器发送 SYNC 命令来完成。该命令执行步骤：
　　1、从服务器向主服务器发送 SYNC 命令
　　2、收到 SYNC 命令的主服务器执行 BGSAVE 命令，在后台生成一个 RDB 文件，并使用一个缓冲区记录从开始执行的所有写命令
　　3、当主服务器的 BGSAVE 命令执行完毕时，主服务器会将 BGSAVE 命令生成的 RDB 文件发送给从服务器，从服务器接收此 RDB 文件，并将服务器状态更新为RDB文件记录的状态。
　　4、主服务器将缓冲区的所有写命令也发送给从服务器，从服务器执行相应命令。
②、命令传播
　　当同步操作完成之后，主服务器会进行相应的修改命令，这时候从服务器和主服务器状态就会不一致。
　　为了让主服务器和从服务器保持状态一致，主服务器需要对从服务器执行命令传播操作，主服务器会将自己的写命令发送给从服务器执行。从服务器执行相应的命令之后，主从服务器状态继续保持一致。
　　总结：通过同步操作以及命令传播功能，能够很好的保证了主从一致的特性。
主从复制的缺点
　　主从复制虽然解决了主节点的单点故障问题，但是由于所有的写操作都是在 Master 节点上操作，然后同步到 Slave 节点，那么同步就会有一定的延时，当系统很繁忙的时候，延时问题就会更加严重，而且会随着从节点slave的增多而愈加严重。
Redis Sentinel 
通过前面的配置，主节点Master 只有一个，一旦主节点挂掉之后，从节点没法担起主节点的任务，那么整个系统也无法运行。如果主节点挂掉之后，从节点能够自动变成主节点，那么问题就解决了，于是哨兵模式诞生了。
　　哨兵模式就是不时地监控redis是否按照预期良好地运行（至少是保证主节点是存在的），若一台主机出现问题时，哨兵会自动将该主机下的某一个从机设置为新的主机，并让其他从机和新主机建立主从关系。
PS：哨兵模式也存在单点故障问题，如果哨兵机器挂了，那么就无法进行监控了，解决办法是哨兵也建立集群，Redis哨兵模式是支持集群的。

为啥在项目里要用缓存呢？
用缓存，主要是俩用途，高性能和高并发
问题
1）缓存与数据库双写不一致
2）缓存雪崩
3）缓存穿透
4）缓存并发竞争
redis的线程模型
1）文件事件处理器
redis基于reactor模式开发了网络事件处理器，这个处理器叫做文件事件处理器，file event handler。这个文件事件处理器，是单线程的，redis才叫做单线程的模型，采用IO多路复用机制同时监听多个socket，根据socket上的事件来选择对应的事件处理器来处理这个事件。
如果被监听的socket准备好执行accept、read、write、close等操作的时候，跟操作对应的文件事件就会产生，这个时候文件事件处理器就会调用之前关联好的事件处理器来处理这个事件。
文件事件处理器是单线程模式运行的，但是通过IO多路复用机制监听多个socket，可以实现高性能的网络通信模型，又可以跟内部其他单线程的模块进行对接，保证了redis内部的线程模型的简单性。
文件事件处理器的结构包含4个部分：多个socket，IO多路复用程序，文件事件分派器，事件处理器（命令请求处理器、命令回复处理器、连接应答处理器，等等）。
多个socket可能并发的产生不同的操作，每个操作对应不同的文件事件，但是IO多路复用程序会监听		多个socket，但是会将socket放入一个队列中排队，每次从队列中取出一个socket给事件分派器，事件分派器把socket给对应的事件处理器。
然后一个socket的事件处理完之后，IO多路复用程序才会将队列中的下一个socket给事件分派器。文件事件分派器会根据每个socket当前产生的事件，来选择对应的事件处理器来处理。
2）文件事件
当socket变得可读时（比如客户端对redis执行write操作，或者close操作），或者有新的可以应答的sccket出现时（客户端对redis执行connect操作），socket就会产生一个AE_READABLE事件。
当socket变得可写的时候（客户端对redis执行read操作），socket会产生一个AE_WRITABLE事件。
IO多路复用程序可以同时监听AE_REABLE和AE_WRITABLE两种事件，要是一个socket同时产生了AE_READABLE和AE_WRITABLE两种事件，那么文件事件分派器优先处理AE_REABLE事件，然后才是AE_WRITABLE事件。
3）文件事件处理器
如果是客户端要连接redis，那么会为socket关联连接应答处理器
如果是客户端要写数据到redis，那么会为socket关联命令请求处理器
如果是客户端要从redis读数据，那么会为socket关联命令回复处理器
4）客户端与redis通信的一次流程
在redis启动初始化的时候，redis会将连接应答处理器跟AE_READABLE事件关联起来，接着如果一个客户端跟redis发起连接，此时会产生一个AE_READABLE事件，然后由连接应答处理器来处理跟客户端建立连接，创建客户端对应的socket，同时将这个socket的AE_READABLE事件跟命令请求处理器关联起来。
当客户端向redis发起请求的时候（不管是读请求还是写请求，都一样），首先就会在socket产生一个AE_READABLE事件，然后由对应的命令请求处理器来处理。这个命令请求处理器就会从socket中读取请求相关数据，然后进行执行和处理。
    接着redis这边准备好了给客户端的响应数据之后，就会将socket的AE_WRITABLE事件跟命令回复处理器关联起来，当客户端这边准备好读取响应数据时，就会在socket上产生一个AE_WRITABLE事件，会由对应的命令回复处理器来处理，就是将准备好的响应数据写入socket，供客户端来读取。
命令回复处理器写完之后，就会删除这个socket的AE_WRITABLE事件和命令回复处理器的关联关系。
（3）为啥redis单线程模型也能效率这么高？
1）纯内存操作
2）核心是基于非阻塞的IO多路复用机制
3）单线程反而避免了多线程的频繁上下文切换问题（百度）
redis都有哪些数据类型？分别在哪些场景下使用比较合适？
（1）string
这是最基本的类型了，没啥可说的，就是普通的set和get，做简单的kv缓存
（2）hash
这个是类似map的一种结构。hash类的数据结构，主要是用来存放一些对象，把一些简单的对象给缓存起来，后续操作的时候，你可以直接仅仅修改这个对象中的某个字段的值
（3）list
有序列表
（4）set
无序集合，自动去重
（5）sorted set
排序的set，去重但是可以排序，写进去的时候给一个分数，自动根据分数排序
redis的过期策略都有哪些？内存淘汰机制都有哪些？手写一下LRU代码实现？
定期删除+惰性删除
所谓定期删除，指的是redis默认是每隔100ms就随机抽取一些设置了过期时间的key，检查其是否过期，如果过期就删除。假设redis里放了10万个key，都设置了过期时间，你每隔几百毫秒，就检查10万个key，那redis基本上就死了，cpu负载会很高的，消耗在你的检查过期key上了。注意，这里可不是每隔100ms就遍历所有的设置过期时间的key，那样就是一场性能上的灾难。实际上redis是每隔100ms随机抽取一些key来检查和删除的。
惰性删除，在你获取某个key的时候，redis会检查一下 ，这个key如果设置了过期时间那么是否过期了？如果过期了此时就会删除，不会给你返回任何东西。
内存淘汰
1）noeviction：当内存不足以容纳新写入数据时，新写入操作会报错
2）allkeys-lru：当内存不足以容纳新写入数据时，在键空间中，移除最近最少使用的key（这个是最常用的）
3）allkeys-random：当内存不足以容纳新写入数据时，在键空间中，随机移除某个key
4）volatile-lru：当内存不足以容纳新写入数据时，在设置了过期时间的键空间中，移除最近最少使用的key
5）volatile-random：当内存不足以容纳新写入数据时，在设置了过期时间的键空间中，随机移除某个key
6）volatile-ttl：当内存不足以容纳新写入数据时，在设置了过期时间的键空间中，有更早过期时间的key优先移除
要不你手写一个LRU算法？
LRU（Least recently used，最近最少使用）
https://blog.csdn.net/nakiri_arisu/article/details/79205660
利用LinkedHashMap实现的简单LRU：
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
private final int CACHE_SIZE;
 
    // 这里就是传递进来最多能缓存多少数据
    public LRUCache(int cacheSize) {
         // 这块就是设置一个hashmap的初始大小，同时最后一个true指的是让linkedhashmap按照访问顺序来进行排序，
         //最近访问的放在头，最老访问的就在尾
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        CACHE_SIZE = cacheSize;
    }
 
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        // 这个意思就是说当map中的数据量大于指定的缓存个数的时候，就自动删除最老的数据
        return size() > CACHE_SIZE; 
    }


如何保证Redis的高并发和高可用？
redis主从架构 -> 读写分离架构 -> 可支持水平扩展的读高并发架构

redis的主从复制原理能介绍一下么？
redis replication的核心机制
（1）redis采用异步方式复制数据到slave节点，不过redis 2.8开始，slave node会周期性地确认自己每次复制的数据量
（2）一个master node是可以配置多个slave node的
（3）slave node也可以连接其他的slave node
（4）slave node做复制的时候，是不会block master node的正常工作的
（5）slave node在做复制的时候，也不会block对自己的查询操作，它会用旧的数据集来提供服务; 但是复制完成的时候，需要删除旧数据集，加载新数据集，这个时候就会暂停对外服务了
（6）slave node主要用来进行横向扩容，做读写分离，扩容的slave node可以提高读的吞吐量
如果采用了主从架构，那么建议必须开启master node的持久化！
1、主从架构的核心原理
当启动一个slave node的时候，它会发送一个PSYNC命令给master node
如果这是slave node重新连接master node，那么master node仅仅会复制给slave部分缺少的数据; 否则如果是slave node第一次连接master node，那么会触发一次full resynchronization
开始full resynchronization的时候，master会启动一个后台线程，开始生成一份RDB快照文件，同时还会将从客户端收到的所有写命令缓存在内存中。RDB文件生成完毕之后，master会将这个RDB发送给slave，slave会先写入本地磁盘，然后再从本地磁盘加载到内存中。然后master会将内存中缓存的写命令发送给slave，slave也会同步这些数据。
slave node如果跟master node有网络故障，断开了连接，会自动重连。master如果发现有多个slave node都来重新连接，仅仅会启动一个rdb save操作，用一份数据服务所有slave node。
2、主从复制的断点续传
从redis 2.8开始，就支持主从复制的断点续传，如果主从复制过程中，网络连接断掉了，那么可以接着上次复制的地方，继续复制下去，而不是从头开始复制一份
master node会在内存中常见一个backlog，master和slave都会保存一个replica offset还有一个master id，offset就是保存在backlog中的。如果master和slave网络连接断掉了，slave会让master从上次的replica offset开始继续复制
但是如果没有找到对应的offset，那么就会执行一次resynchronization
3、无磁盘化复制
master在内存中直接创建rdb，然后发送给slave，不会在自己本地落地磁盘了
repl-diskless-sync
repl-diskless-sync-delay，等待一定时长再开始复制，因为要等更多slave重新连接过来
4、过期key处理
slave不会过期key，只会等待master过期key。如果master过期了一个key，或者通过LRU淘汰了一个key，那么会模拟一条del命令发送给slave。
1、复制的完整流程
（1）slave node启动，仅仅保存master node的信息，包括master node的host和ip，但是复制流程没开始
master host和ip是从哪儿来的，redis.conf里面的slaveof配置的
（2）slave node内部有个定时任务，每秒检查是否有新的master node要连接和复制，如果发现，就跟master node建立socket网络连接
（3）slave node发送ping命令给master node
（4）口令认证，如果master设置了requirepass，那么salve node必须发送masterauth的口令过去进行认证
（5）master node第一次执行全量复制，将所有数据发给slave node
（6）master node后续持续将写命令，异步复制给slave node
2、数据同步相关的核心机制
指的就是第一次slave连接msater的时候，执行的全量复制，那个过程里面你的一些细节的机制
（1）master和slave都会维护一个offset
master会在自身不断累加offset，slave也会在自身不断累加offset
slave每秒都会上报自己的offset给master，同时master也会保存每个slave的offset
这个倒不是说特定就用在全量复制的，主要是master和slave都要知道各自的数据的offset，才能知道互相之间的数据不一致的情况
（2）backlog
master node有一个backlog，默认是1MB大小
master node给slave node复制数据时，也会将数据在backlog中同步写一份
backlog主要是用来做全量复制中断候的增量复制的
（3）master run id
info server，可以看到master run id
如果根据host+ip定位master node，是不靠谱的，如果master node重启或者数据出现了变化，那么slave node应该根据不同的run id区分，run id不同就做全量复制
如果需要不更改run id重启redis，可以使用redis-cli debug reload命令
（4）psync
从节点使用psync从master node进行复制，psync runid offset
master node会根据自身的情况返回响应信息，可能是FULLRESYNC runid offset触发全量复制，可能是CONTINUE触发增量复制
3、全量复制
（1）master执行bgsave，在本地生成一份rdb快照文件
（2）master node将rdb快照文件发送给salve node，如果rdb复制时间超过60秒（repl-timeout），那么slave node就会认为复制失败，可以适当调节大这个参数
（3）对于千兆网卡的机器，一般每秒传输100MB，6G文件，很可能超过60s
（4）master node在生成rdb时，会将所有新的写命令缓存在内存中，在salve node保存了rdb之后，再将新的写命令复制给salve node
（5）client-output-buffer-limit slave 256MB 64MB 60，如果在复制期间，内存缓冲区持续消耗超过64MB，或者一次性超过256MB，那么停止复制，复制失败
（6）slave node接收到rdb之后，清空自己的旧数据，然后重新加载rdb到自己的内存中，同时基于旧的数据版本对外提供服务
（7）如果slave node开启了AOF，那么会立即执行BGREWRITEAOF，重写AOF

rdb生成、rdb通过网络拷贝、slave旧数据的清理、slave aof rewrite，很耗费时间
如果复制的数据量在4G~6G之间，那么很可能全量复制时间消耗到1分半到2分钟
4、增量复制
（1）如果全量复制过程中，master-slave网络连接断掉，那么salve重新连接master时，会触发增量复制
（2）master直接从自己的backlog中获取部分丢失的数据，发送给slave node，默认backlog就是1MB
（3）msater就是根据slave发送的psync中的offset来从backlog中获取数据的
5、heartbeat
主从节点互相都会发送heartbeat信息
master默认每隔10秒发送一次heartbeat，salve node每隔1秒发送一个heartbeat
6、异步复制
master每次接收到写命令之后，现在内部写入数据，然后异步发送给slave node
redis的哨兵原理能介绍一下么？
1、哨兵的介绍
sentinal，中文名是哨兵
（1）集群监控，负责监控redis master和slave进程是否正常工作
（2）消息通知，如果某个redis实例有故障，那么哨兵负责发送消息作为报警通知给管理员
（3）故障转移，如果master node挂掉了，会自动转移到slave node上
（4）配置中心，如果故障转移发生了，通知client客户端新的master地址
哨兵本身也是分布式的，作为一个哨兵集群去运行，互相协同工作
（1）故障转移时，判断一个master node是宕机了，需要大部分的哨兵都同意才行，涉及到了分布式选举的问题
（2）即使部分哨兵节点挂掉了，哨兵集群还是能正常工作的
2、哨兵的核心知识
（1）哨兵至少需要3个实例，来保证自己的健壮性
（2）哨兵 + redis主从的部署架构，是不会保证数据零丢失的，只能保证redis集群的高可用性
（3）对于哨兵 + redis主从这种复杂的部署架构，尽量在测试环境和生产环境，都进行充足的测试和演练
3、为什么redis哨兵集群只有2个节点无法正常工作？
哨兵集群必须部署2个以上节点
如果哨兵集群仅仅部署了个2个哨兵实例，quorum=1
Configuration: quorum = 1
master宕机，s1和s2中只要有1个哨兵认为master宕机就可以还行切换，同时s1和s2中会选举出一个哨兵来执行故障转移
同时这个时候，需要majority，也就是大多数哨兵都是运行的，2个哨兵的majority就是2（2的majority=2，3的majority=2，5的majority=3，4的majority=2），2个哨兵都运行着，就可以允许执行故障转移
但是如果整个M1和S1运行的机器宕机了，那么哨兵只有1个了，此时就没有majority来允许执行故障转移，虽然另外一台机器还有一个R1，但是故障转移不会执行
4、经典的3节点哨兵集群
       +----+
       | M1 |
       | S1 |
       +----+
          |
+----+    |    +----+
| R2 |----+----| R3 |
| S2 |         | S3 |
+----+         +----+
Configuration: quorum = 2，majority
如果M1所在机器宕机了，那么三个哨兵还剩下2个，S2和S3可以一致认为master宕机，然后选举出一个来执行故障转移
同时3个哨兵的majority是2，所以还剩下的2个哨兵运行着，就可以允许执行故障转移
1、两种数据丢失的情况
主备切换的过程，可能会导致数据丢失
（1）异步复制导致的数据丢失
因为master -> slave的复制是异步的，所以可能有部分数据还没复制到slave，master就宕机了，此时这些部分数据就丢失了
（2）脑裂导致的数据丢失
脑裂，也就是说，某个master所在机器突然脱离了正常的网络，跟其他slave机器不能连接，但是实际上master还运行着
此时哨兵可能就会认为master宕机了，然后开启选举，将其他slave切换成了master
这个时候，集群里就会有两个master，也就是所谓的脑裂
此时虽然某个slave被切换成了master，但是可能client还没来得及切换到新的master，还继续写向旧master的数据可能也丢失了
因此旧master再次恢复的时候，会被作为一个slave挂到新的master上去，自己的数据会清空，重新从新的master复制数据
2、解决异步复制和脑裂导致的数据丢失
min-slaves-to-write 1
min-slaves-max-lag 10
要求至少有1个slave，数据复制和同步的延迟不能超过10秒
如果说一旦所有的slave，数据复制和同步的延迟都超过了10秒钟，那么这个时候，master就不会再接收任何请求了
上面两个配置可以减少异步复制和脑裂导致的数据丢失
（1）减少异步复制的数据丢失
有了min-slaves-max-lag这个配置，就可以确保说，一旦slave复制数据和ack延时太长，就认为可能master宕机后损失的数据太多了，那么就拒绝写请求，这样可以把master宕机时由于部分数据未同步到slave导致的数据丢失降低的可控范围内
（2）减少脑裂的数据丢失
如果一个master出现了脑裂，跟其他slave丢了连接，那么上面两个配置可以确保说，如果不能继续给指定数量的slave发送数据，而且slave超过10秒没有给自己ack消息，那么就直接拒绝客户端的写请求
这样脑裂后的旧master就不会接受client的新数据，也就避免了数据丢失
上面的配置就确保了，如果跟任何一个slave丢了连接，在10秒后发现没有slave给自己ack，那么就拒绝新的写请求
因此在脑裂场景下，最多就丢失10秒的数据
1、sdown和odown转换机制
sdown是主观宕机，就一个哨兵如果自己觉得一个master宕机了，那么就是主观宕机
odown是客观宕机，如果quorum数量的哨兵都觉得一个master宕机了，那么就是客观宕机
sdown达成的条件很简单，如果一个哨兵ping一个master，超过了is-master-down-after-milliseconds指定的毫秒数之后，就主观认为master宕机
sdown到odown转换的条件很简单，如果一个哨兵在指定时间内，收到了quorum指定数量的其他哨兵也认为那个master是sdown了，那么就认为是odown了，客观认为master宕机
2、哨兵集群的自动发现机制
哨兵互相之间的发现，是通过redis的pub/sub系统实现的，每个哨兵都会往__sentinel__:hello这个channel里发送一个消息，这时候所有其他哨兵都可以消费到这个消息，并感知到其他的哨兵的存在
每隔两秒钟，每个哨兵都会往自己监控的某个master+slaves对应的__sentinel__:hello channel里发送一个消息，内容是自己的host、ip和runid还有对这个master的监控配置
每个哨兵也会去监听自己监控的每个master+slaves对应的__sentinel__:hello channel，然后去感知到同样在监听这个master+slaves的其他哨兵的存在
每个哨兵还会跟其他哨兵交换对master的监控配置，互相进行监控配置的同步
3、slave配置的自动纠正
哨兵会负责自动纠正slave的一些配置，
比如slave如果要成为潜在的master候选人，哨兵会确保slave在复制现有master的数据; 
如果slave连接到了一个错误的master上，比如故障转移之后，那么哨兵会确保它们连接到正确的master上
4、slave->master选举算法
如果一个master被认为odown了，而且majority哨兵都允许了主备切换，那么某个哨兵就会执行主备切换操作，此时首先要选举一个slave来
会考虑slave的一些信息
（1）跟master断开连接的时长
（2）slave优先级
（3）复制offset
（4）run id
如果一个slave跟master断开连接已经超过了down-after-milliseconds的10倍，外加master宕机的时长，那么slave就被认为不适合选举为master
(down-after-milliseconds * 10) + milliseconds_since_master_is_in_SDOWN_state
接下来会对slave进行排序
（1）按照slave优先级进行排序，slave priority越低，优先级就越高
（2）如果slave priority相同，那么看replica offset，哪个slave复制了越多的数据，offset越靠后，优先级就越高
（3）如果上面两个条件都相同，那么选择一个run id比较小的那个slave
5、quorum和majority
quorum：确认odown的最少的哨兵数量
majority：授权进行主从切换的最少的哨兵数量
每次一个哨兵要做主备切换，首先需要quorum数量的哨兵认为odown，然后选举出一个哨兵来做切换，这个哨兵还得得到majority哨兵的授权，才能正式执行切换
如果quorum < majority，比如5个哨兵，majority就是3，quorum设置为2，那么就3个哨兵授权就可以执行切换
但是如果quorum >= majority，那么必须quorum数量的哨兵都授权，比如5个哨兵，quorum是5，那么必须5个哨兵都同意授权，才能执行切换
6、configuration epoch
哨兵会对一套redis master+slave进行监控，有相应的监控的配置
执行切换的那个哨兵，会从要切换到的新master（salve->master）那里得到一个configuration epoch，这就是一个version号，每次切换的version号都必须是唯一的
如果第一个选举出的哨兵切换失败了，那么其他哨兵，会等待failover-timeout时间，然后接替继续执行切换，此时会重新获取一个新的configuration epoch，作为新的version号
7、configuraiton传播
哨兵完成切换之后，会在自己本地更新生成最新的master配置，然后同步给其他的哨兵，就是通过之前说的pub/sub消息机制
这里之前的version号就很重要了，因为各种消息都是通过一个channel去发布和监听的，所以一个哨兵完成一次新的切换之后，新的master配置是跟着新的version号的
其他的哨兵都是根据版本号的大小来更新自己的master配置的
1、RDB和AOF两种持久化机制的介绍
RDB持久化机制，对redis中的数据执行周期性的持久化
AOF机制对每条写入命令作为日志，以append-only的模式写入一个日志文件中，在redis重启的时候，可以通过回放AOF日志中的写入指令来重新构建整个数据集
2、RDB持久化机制的优点
（1）RDB会生成多个数据文件，每个数据文件都代表了某一个时刻中redis的数据，这种多个数据文件的方式，非常适合做冷备，可以将这种完整的数据文件发送到一些远程的安全存储上去
（2）RDB对redis对外提供的读写服务，影响非常小，可以让redis保持高性能，因为redis主进程只需要fork一个子进程，让子进程执行磁盘IO操作来进行RDB持久化即可
（3）相对于AOF持久化机制来说，直接基于RDB数据文件来重启和恢复redis进程，更加快速
3、RDB持久化机制的缺点
（1）如果想要在redis故障时，尽可能少的丢失数据，那么RDB没有AOF好。一般来说，RDB数据快照文件，都是每隔5分钟，或者更长时间生成一次，这个时候就得接受一旦redis进程宕机，那么会丢失最近5分钟的数据
（2）RDB每次在fork子进程来执行RDB快照数据文件生成的时候，如果数据文件特别大，可能会导致对客户端提供的服务暂停数毫秒，或者甚至数秒
4、AOF持久化机制的优点
（1）AOF可以更好的保护数据不丢失，一般AOF会每隔1秒，通过一个后台线程执行一次fsync操作，最多丢失1秒钟的数据
（2）AOF日志文件以append-only模式写入，所以没有任何磁盘寻址的开销，写入性能非常高，而且文件不容易破损，即使文件尾部破损，也很容易修复
（3）AOF日志文件即使过大的时候，出现后台重写操作，也不会影响客户端的读写。因为在rewrite log的时候，会对其中的指导进行压缩，创建出一份需要恢复数据的最小日志出来。再创建新日志文件的时候，老的日志文件还是照常写入。当新的merge后的日志文件ready的时候，再交换新老日志文件即可。
（4）AOF日志文件的命令通过非常可读的方式进行记录，这个特性非常适合做灾难性的误删除的紧急恢复。比如某人不小心用flushall命令清空了所有数据，只要这个时候后台rewrite还没有发生，那么就可以立即拷贝AOF文件，将最后一条flushall命令给删了，然后再将该AOF文件放回去，就可以通过恢复机制，自动恢复所有数据
5、AOF持久化机制的缺点
（1）对于同一份数据来说，AOF日志文件通常比RDB数据快照文件更大
（2）AOF开启后，支持的写QPS会比RDB支持的写QPS低，因为AOF一般会配置成每秒fsync一次日志文件，当然，每秒一次fsync，性能也还是很高的
（3）以前AOF发生过bug，就是通过AOF记录的日志，进行数据恢复的时候，没有恢复一模一样的数据出来。所以说，类似AOF这种较为复杂的基于命令日志/merge/回放的方式，比基于RDB每次持久化一份完整的数据快照文件的方式，更加脆弱一些，容易有bug。不过AOF就是为了避免rewrite过程导致的bug，因此每次rewrite并不是基于旧的指令日志进行merge的，而是基于当时内存中的数据进行指令的重新构建，这样健壮性会好很多。
6、RDB和AOF到底该如何选择
（1）不要仅仅使用RDB，因为那样会导致你丢失很多数据
（2）也不要仅仅使用AOF，因为那样有两个问题，第一，你通过AOF做冷备，没有RDB做冷备，来的恢复速度更快; 第二，RDB每次简单粗暴生成数据快照，更加健壮，可以避免AOF这种复杂的备份和恢复机制的bug
（3）综合使用AOF和RDB两种持久化机制，用AOF来保证数据不丢失，作为数据恢复的第一选择; 用RDB来做不同程度的冷备，在AOF文件都丢失或损坏不可用的时候，还可以使用RDB来进行快速的数据恢复
2.4、重写机制
随着命令不断从AOF缓存中写入到AOF文件中，AOF文件会越来越大，为了解决这个问题，Redis引入了AOF重写机制来压缩AOF文件。
	AOF文件的压缩和RDB文件的压缩原理不一样，RDB文件的压缩是使用压缩算法将二进制的RDB文件压缩，而AOF文件的压缩主要是去除AOF文件中的无效命令，比如说： 
- 同一个key的多次写入只保留最后一个命令 
- 已删除、已过期的key的写命令不再保留
AOF重写的触发机制也分为手动触发和自动触发两种方式。
手动触发
执行bgrewriteaof命令直接触发AOF重写
自动触发
在redis.config配置文件中有两个配置项
auto-aof-rewrite-min-size 64MB
auto-aof-rewrite-min-percenrage 100

上面两个配置表示： 
- 当AOF文件小于64MB的时候不进行AOF重写 
- 当当前AOF文件比上次AOF重写后的文件大100%的时候进行AOF重写
可以在redis.conf配置文件中添加这两个参数来自动触发AOF重写，执行bgrewriteaof命令
4、总结
关于Redis的两种持久化方式到这里就介绍完了，这里再总结一下： 
- RDB持久化基于内存快照存储二进制文件，AOF持久化基于写命令存储文本文件。 
- RDB文件采用了压缩算法，比较小；AOF文件随着命令的叠加会越来越大，Redis提供了AOF重写来压缩AOF文件。 
- 恢复RDB文件的速度比AOF文件快很多。 
- RDB持久化方式实时性不好，所以AOF持久化更主流。 
- 合理的使用AOF的同步策略，理论上不会丢失大量的数据。
深入Redis的RDB和AOF两种持久化方式以及AOF重写机制的分析
https://blog.csdn.net/Leon_cx/article/details/81545178
史上最全Redis面试题(含答案):哨兵+复制+事务+集群+持久化等
https://blog.csdn.net/qq_41699100/article/details/86102235

缓存雪崩

缓存雪崩的事前事中事后的解决方案
事前：redis高可用，主从+哨兵，redis cluster，避免全盘崩溃
事中：本地ehcache缓存 + hystrix限流&降级，避免MySQL被打死
事后：redis持久化，快速恢复缓存数据
缓存穿透
返回默认值



