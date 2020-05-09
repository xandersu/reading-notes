# 4-02 Redis常用数据类型

# 供用户使用的数据类型

- string:最基本的数据类型，二进制安全。KV键值对，值最大存储512M。redis string可以包含任何数据，例jpg图片，或者序列化的对象。
 set name "redis"; => OK
  
  get name; => "redis";
  set count 1;=> OK
  get count;=> "1"
  incr count;=> (integer)2
  
  get count;=> "2"
  
  redi是、单个操作是原子性的，一个事务是一个不可分割的最小工作单位，事务中包含的操作，要么都做，要么都不做。可以利用原子操作incr实现计数功能。
  
  ```
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
  ```
比C字符串，SDS有以下特点
1. 常数复杂度获取字符串长度
2. 杜绝缓冲区溢出
3. 减少修改字符串长度所需的内存重分配次数
4. 二进制安全
5. 兼容部分C字符串函数
  
  
- Hash:string元素组成的字典，适合用于存储对象
  
  ```
  hmset lilei namr "lilei" age 26 title "XXX"
  hget lilei age
  hget lilei title
  hset lilei title "yyyy"
  ```
  
- List:列表，按照String元素插入顺序排序，可以添加一个元素到头部或者尾部。

  ```
  lpush mylist aaa
  lrange mylist 0 10
  ```

- Set:String 元素组成的无序集合，通过hash表实现，不允许重复。

  ```
  sadd myset 111
  sadd myset 111 -> 0
  smembers myset
  ```
  
  redis为set提供求交集、并集、差集操作。
  
- Sorted Set: 通过分数来为集合中的成员进行从小到大的排序。

  ```
  zadd myzset 3 abc
  zadd myzset 1 abb
  zadd myzset 2 abb -> 0
  zrangebyscore myzset 0 10
  ```
  
- 用于计数的HyperLogLog，用于支持存储地理位置信息的Geo

  
  
# 底层数据类型基础

1. 简单动态字符串
2. 链表
3. 字典
4. 跳跃表
5. 整数集合
6. 压缩列表
7. 对象

```
typedef struct listNode{

//前置节点
struct listNode *prev;
//后置节点
struct listNode *next;
//节点的值
void *value;
}
```

```
typedef struct list {
// 表头指针
listNode *head;
// 表尾指针
listNode *tail;
// 节点数量
unsigned long len;
// 复制函数
void *(*dup)(void *ptr);
// 释放函数
void (*free)(void *ptr);
// 比对函数
int (*match)(void *ptr, void *key);
} list;
```

– 节点带有前驱和后继指针，访问前驱节点和后继节点的复杂度为O(1) ，并且对链表
的迭代可以在从表头到表尾和从表尾到表头两个方向进行；
– 链表带有指向表头和表尾的指针，因此对表头和表尾进行处理的复杂度为O(1) ；
– 链表带有记录节点数量的属性，所以可以在O(1) 复杂度内返回链表的节点数量（长
度）；

## 字典
字典（dictionary），又名映射（map）或关联数组（associative array）

用于保存键值对（key-value pairs）组成，各个键值对的键各不相同，程序可以将新的键值对
添加到字典中，或者基于键进行查找、更新或删除等操作。

字典底层使用哈希表，一个哈希表内可以有多个哈希表节点，而每个哈希表节点保存了字典中的一个键值对



```

/*
* 哈希表
*/
typedef struct dictht {
// 哈希表节点指针数组（俗称桶，bucket）
dictEntry **table;
// 指针数组的大小
unsigned long size;
// 指针数组的长度掩码，用于计算索引值
unsigned long sizemask;
// 哈希表现有的节点数量
unsigned long used;
} dictht;

/*
* 字典
**
每个字典使用两个哈希表，用于实现渐进式rehash
*/
typedef struct dict {
// 特定于类型的处理函数
dictType *type;
// 类型处理函数的私有数据
void *privdata;
// 哈希表（2 个）
dictht ht[2];
// 记录rehash 进度的标志，值为-1 表示rehash 未进行
int rehashidx;
// 当前正在运作的安全迭代器数量
int iterators;
} ddict;
```
• 字典由键值对构成的抽象数据结构。
• Redis 中的数据库和哈希键都基于字典来实现。
• Redis 字典的底层实现为哈希表，每个字典使用两个哈希表，一般情况下只使用0 号哈希
表，只有在rehash 进行时，才会同时使用0 号和1 号哈希表。
• 哈希表使用链地址法来解决键冲突的问题。
• Rehash 可以用于扩展或收缩哈希表。
• 对哈希表的rehash 是分多次、渐进式地进行的。

## 跳跃表

跳表只在有序集合和集群节点中用作内部数据结构。

• 表头（head）：负责维护跳跃表的节点指针。
• 表尾(tail)：全部由NULL 组成，表示跳跃表的末尾。
• 跳跃表节点：保存着元素值，以及多个层。
• 层(level)：保存着指向其他元素的指针。高层的指针越过的元素数量大于等于低层的指针，为了
提高查找的效率，程序总是从高层先开始访问，然后随着元素值范围的缩小，慢慢降低层
次。
• 跳跃表长度length:



跳跃表是一种随机化数据结构，它的查找、添加、删除操作都可以在对数期望时间下完
成。
• 跳跃表目前在Redis 的唯一作用就是作为有序集类型的底层数据结构（之一，另一个构
成有序集的结构是字典）。
• 为了适应自身的需求，Redis 基于William Pugh 论文中描述的跳跃表进行了修改，包括：
1. score 值可重复。
2. 对比一个元素需要同时检查它的score 和memeber 。
3. 每个节点带有高度为1 层的后退指针，用于从表尾方向向表头方向迭代。
1.4.