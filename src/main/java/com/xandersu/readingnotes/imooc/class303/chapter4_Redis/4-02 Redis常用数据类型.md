# 4-02 Redis常用数据类型

# 供用户使用的数据类型

- string:最基本的数据类型，二进制安全。KV键值对，值最大存储512M。redis string可以包含任何数据，例jpg图片，或者序列化的对象。 set name "redis";set count 1;get count;incr count;

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



  