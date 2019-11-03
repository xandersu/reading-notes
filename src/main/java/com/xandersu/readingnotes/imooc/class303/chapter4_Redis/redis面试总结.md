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











