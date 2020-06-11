# HBase简介

- HBase是一个分布式的、面向列的开源数据库
- HBase在Hadoop之上提供了类似于Bigtable的能力
- HBase不同于一般的关系数据库，它适合非结构化数据存储



## Bigtable是什么？

压缩的、高性能的、高可扩展性的，基于谷歌文件系统gfs的数据库，用于存储大规模的结构化数据，在扩展性和性能上有很大的优势。

## 什么事面向列的数据库？

 把每一列中的数据值放到一起进行存储，对应的是行式数据库。

行式数据库可以理解为关系型数据库，一行一行的进行存储

##  为什么HBase适合存储非结构化数据

结构化数据：可以用二维表格形式存储的数据称为结构化数据

非结构化数据：图片、文档

可以将图片、文档以二进制的形式存储到HBase中



- HBase基于Hadoop的核心HDFS系统进行存储，类似于hive
- HBase可以存储超大数据并适合用来进行大数据的实时查询

## HBase与HDFS

- HBase建立在Hadoop文件系统之上，利用了Hadoop的文件系统的容错能力
- HBase提供对数据的随机实时读写访问功能
- HBase内部使用哈希表，并存储索引，可将在HDFS文件中的数据进行快速查找



## HBase使用场景

- 瞬间写入量很大，常用数据库不好支撑或者需要很高的支撑成本
- 数据需要长久保存，且量会持久增长到比较大的场景
- HBase不适合有join，多级索引，表关系复杂的数据结构模型



HBase 是 CP，强一致性的



## HBase的概念

- NameSpace：数据库
- Table：表名必须是能用在文件路径中的合法名字
- Row：表里，每一行代表一个数据对象，每一行都是以一个行键（Row Key）来进行唯一标识，没有特定的数据类型，以二进制字节存储
- Column：HBase的列由Column family和Column qualifier组成，由冒号（:）进行间隔，比如 AAA:BBB
- Row Key:唯一标识一行记录，不可以改变
- Column family：列族，在定义HBase表时需要提前设置好列族，表中所有列都需要组织在列族里。Column family是Column 集合
- Column qualifier：列族中数据通过列标识来进行映射，可以理解为一个键值对，Column qualifier就是key
- Cell：每一行键，列族和列标识共同组成一个单元
- Timestamp：每一个值都会有一个Timestamp，作为值特定版本的标识符



RDBMS关系型数据库的接口

| ID   | FILE NAME | FILE PATH  | FILE TYPE | FILE SIZE | CREATOR |
| ---- | --------- | ---------- | --------- | --------- | ------- |
| I    | file.txt  | /home      | txt       | 1024      | Tom     |
| 2    | file2.jpg | /home/pics | jpg       | 5032      | Jerry   |

HBase的结构

| RowKey | FileInfo                                     | SaveInfo                           |
| ------ | -------------------------------------------- | ---------------------------------- |
| 1      | name:file.txt<br />type:txt<br />size:1024   | path:/home<br />creator:Tom        |
| 2      | name:file2.jppg<br />type:jpg<br />size:5032 | path:/home/pics<br />creator:Jerry |



# Hadoop、HBase伪分布式集群安装

//TODO



## HBase基础架构

Java API

HM Master Region Server Region Server      Zookeeper

HDFS



### HM Master

Master-Slave结构

- HMaster是HBase主从集群架构中的中央节点
- HMaster将region 分配给 Region Server，协调Region Server的负载并维护集群的状态
- 维护表和region的元数据，不参与数据的输入/输出过程

### Region Server

- 维护HMaster分配给他的region，处理对这些region的IO请求
- 负责切分正在运行过程中变的过大的region

### Zookeeper

- zookeeper是集群的协调器
- HMaster启动将系统表加载到zookeeper
- 提供HBase  Region Server状态信息

