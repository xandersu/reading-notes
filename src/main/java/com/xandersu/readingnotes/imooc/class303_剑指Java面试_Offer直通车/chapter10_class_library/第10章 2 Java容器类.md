# 第10章 2 Java容器类

### 数据结构考点

- 数组和链表的区别；
- 链表的操作，如翻转、链表环路检测、双向链表、循环链表相关操作；
- 队列、栈的应用；
- 二叉树的遍历方式及其递归和非递归的实现；
- 红黑树的旋转；

### 算法考点

- 内部考点：如递归排序、交换排序（冒泡、快排）、选择排序、插入排序；
- 外部排序：应掌握如何利用有限的内存配合海量的外部存储来处理超大的数据集，写不出来也要有相关的思路



### 考点扩展

- 哪些排序是不稳定的，稳定意味着什么
- 不同数据集，各种排序最好或最差的情况
- 如何优化算法



## Java集合框架



[![JOsEh8.png](https://s1.ax1x.com/2020/05/01/JOsEh8.png)](https://imgchr.com/i/JOsEh8)



[![JO6nwn.png](https://s1.ax1x.com/2020/05/01/JO6nwn.png)](https://imgchr.com/i/JO6nwn)



# Map

key - value

key不可重复

value可重复



[![JOcwHs.png](https://s1.ax1x.com/2020/05/01/JOcwHs.png)](https://imgchr.com/i/JOcwHs)



## HashMap

HashMap（Java8之前）：数组+链表

数组长度默认16

hash(key.hashCode())%len：获得要添加的元素在数组中的位置



###   HashMap：put逻辑

1. 如果HashMap未被初始化过，则初始化
2. 对key求hash值，然后在计算下标
3. 如果没有碰撞，直接放入桶中
4. 如果碰撞了，以链表的方式链接到后面
5. 如果链表长度超过阈值8，就把链表转成红黑树
6. 如果链表长度低于6，就把红黑树转回链表
7. 如果节点已存在就替换掉旧值
8. 如果桶装满了（容量16*加载因子0.75），就需要resize（扩容2倍后重排）



### HashMap：如果有效减少碰撞

- 扰动函数：促使元素位置分布均匀，较少碰撞几率
- 使用final对象，并采用合适的equals()和hashCode()方法
  - string，final的，并且重写了equals()和hashCode()方法



### HashMap：从获取hash到散列的过程

```
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

自己的hash值 异或 hash值的高16位。

使散列更均匀



hashmap的最大数量是2的n次方

hash操作的性能更高



### HashMap：扩容问题

- 多进程环境下，调整大小会存在条件竞争，容易造成死锁
- rehashing是一个比较耗时的过程



### 使HashMap线程安全

Collections.synchronizedMap()

HashTable

多线程环境下需要竞争同一把锁



### 如何优化HashTable？

- 锁粒度细化，将整段锁拆解成多个锁进行优化



#### 早期ConcurrentHashMap：通过分段锁Segment来实现

数组+链表



### ConcurrentHashMap

CAS算法

不允许插入null的键值



###   ConcurrentHashMap：put逻辑

1. 判断Node[]数组是否初始化，没有则进行初始化操作
2. 通过Hash定位数组的索引坐标，是否有Node节点，如果没有则使用CAS进行添加（链表的头结点），添加失败则进入下次循环。
3. 检查到内部正在扩容，就帮助它进一步扩容
4. 如果f != null，则使用synchronized锁住f元素（链表/红黑二叉树的头元素）
   1. 如果是Node（链表结构）则执行链表的添加操作
   2. 如果是TreeNode（树形结构）则执行树添加操作
5. 判断链表长度已经达到阈值8，阈值可以调整，当节点数超过这个值就需要把链表转换成树结构

###  

###  ConcurrentHashMap总结：比Segment，锁拆的更细

1. 首先使用无锁CAS插入头结点，失败则循环重试
2. 若头结点已存在，则尝试获取头结点的同步锁，再进行操作



###  ConcurrentHashMap：注意点

1. size()方法和mappingCount()方法的异同，两者计算是否准确？
2. 多线程环境下如何进行扩容？



### HashMap、HashTable、ConcurrentHashMap区别

- HashMap线程不安全，数组+链表+红黑树
- HashTable线程安全，锁住整个对象，数组+链表
- ConcurrentHashMap线程安全，CAS+同步锁，数组+链表—+红黑树
- HashMap key、value可以为null，其他两个不行





















