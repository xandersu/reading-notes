# 第3章 HBase原理与实战

## HBase写流程

client访问zk，获取表的信息以及表的region，根据rowkey获取region server的信息，批量提交的话会将row key根据location进行分组

client得到region server地址后，会向region server进行写请求，region server接收到请求后会进行检查，判断这个region是不是只读状态，大小是否超过memStore size，检查通过后，region server会依次写入memStore和hlog，两个都得写入成功，写入完成。

memStore超过一定阈值，会flush成store file文件，store file文件超过一定数量会触发合并机制，合并成一个大的store file文件。

如果单个store file文件大小达到阈值，触发spin机制，将store file文件一分为二，然后HMaster给两个region分配相应的region server进行管理，从而分担压力。

- client先访问zk，得到对应的region server的地址
- client对region server发起写请求，region server接受数据写内存中
- 当memStore的大小达到一定的值后，flush到StoreFile并存储到HDFS

region server = region + ... + region + HLog

region = Store + ... + Store

Store = MemStore + Store File + ... + Store File

Store File 是HFile的封装

数据存储在region中，每个region只存储一个列族的（一部分）数据，

region达到一定大小后，会根据rowKey的排序换分为多个region，每个region里有包含了多个Store对象，每个Store对象包含了一个memStore和一个或多个store File.

MemStore是数据在内存里的实体，并且一般是有序的，数据先写入到MemStore，当MemStore的大小达到上限后，Store会创建Store File

HLog保证数据不丢死，HLog是WAL（Write ahead log）的实现，WAL是预写日志。

一般WAL是献血日志在写内存的，HBase是先写内存在写日志，依托MVCC模式确保一致性。



## HBase的读流程

client先访问zk，访问meta region server的信息将meta表缓存到本地，然后通过缓存的表获取region server的信息，当client要访问的表在哪个region server之后，client向region server发起读请求，region server处理后会将数据返回客户端。

region server先扫描自己的memStore，如果没找到就扫描block cache，如果没找到就从Store File中查找数据。然后返回client。

- client先访问zk，得到 region server地址
- client对 region server发起读请求
-  region server收到读请求，先扫描自己的MemStore，再扫描BlockCache（加速读内容缓冲区），如果没找到就从Store File读取数据，返回client

HMaster启动的时候会把meta的信息表加载到zooker，这个表里存储了HBase所有的表，所有的region的详细信息，比如region开始的key结束的key，所在region server的地址，HBase的meta表相当于一个目录，通过meta表可以快速的去定位到数据的实际位置，所以读写操作只需要与zk和对应的region server进行交互。

HMaster只负责维护table和region的元数据信息，协调各个region server



## HBase模块协作

HMaster

region server

zk

### HBase启动发生了什么？

- HMaster启动，注册zk，等到region server 汇报
- region server 注册到zk，向HMaster汇报
- 对各个region server （包括失效的）的数据进行整理，分配region和meta信息



### region server 失效了会发生什么？如何保证的数据可读可写的？

- HMaster将失效的region server 上的region分配到其他节点
- HMaster更新hbase：meta表以保证数据正常访问



### HMaster失效了会发生什么？配置了高可用集群会怎么样？没有配置高可用集群会怎么样？

- 配置了高可用：处于Backup状态的其他HMaster节点推选出一个专为Active状态
- 没有配置高可用：数据能正常读写，但不能创建删除表，也不能更改表结构



## HBase shell命令实战

- HBase shell基础命令：status、list、create table等
- HBase shell数据模型操作命令：put、get、delete、scan等

| 名称                         | 命令表达式                                                   |
| ---------------------------- | ------------------------------------------------------------ |
| 查看存在哪些表               | list                                                         |
| 创建表                       | create '表名','列名1','列名2','列名N',                       |
| 添加记录                     | put '表名','行名','列名','值'                                |
| 查看记录                     | get '表名','行名'                                            |
| 查看表中记录总数             | count '表名'                                                 |
| 删除记录                     | delete '表名','行名','列名'                                  |
| 删除一张表                   | 先要屏蔽该表，才能对该表进行删除，第一步'disable' '表名' ，第二部drop '表名' |
| 查看所有记录                 | scan '表名'                                                  |
| 查看某个表中某个列中所有数据 | scan '表名',['列名']                                         |
| 更新记录                     | 重写一遍进行覆盖，就是put命令                                |



```
hbase shell
help 'status'
status 
list
create 'fileTable','fileInfo','saveInfo'
list => 'fileTable'
desc 'fileTable'
alter 'fileTable','cf'
alter 'fileTable',{Name=>'cf',METHOD=>'delete'}
put 'fileTable','rowkey1','fileInfo:name','file1.txt'
put 'fileTable','rowkey1','fileInfo:type','txt'
put 'fileTable','rowkey1','fileInfo:size','1024'
put 'fileTable','rowkey1','saveInfo:path','/home'
put 'fileTable','rowkey1','saveInfo:creator','tom'
count 'fileTable'
get 'fileTable','rowkey1'
get 'fileTable','rowkey1','fileInfo'
scan 'fileTable'
scan 'fileTable',{COLUMN=>'fileInfo:name'}
scan 'fileTable',{COLUMN=>'fileInfo'}
scan 'fileTable',{STARTROW=>'rowkey1',LIMIT=>1,VERSION=>1}
delete 'fileTable','rowkey1','fileInfo:size'
deleteall 'fileTable','rowkey1'
disable 'fileTable'
is_enabled 'fileTable'
is_disabled 'fileTable'
drop 'fileTable'
```

## HBase操作JavaAPI

| Java类            | 对应数据模型 |
| ----------------- | ------------ |
| HBaseConfigration | 配置类       |
| HBaseAdmin        | 管理Admin类  |
| Table             | Table操作类  |
| Put               | 添加         |
| Get               | 单个查询     |
| Scan              | 检索         |
| Result            | 查询的结果   |
| ResultScanner     | 检索结果     |



### HBase过滤器能做什么

- 为筛选数据提供了一组过滤器，通过过滤器可以在hbase中数据的多个维度（行，列，数据版本）上进行对数据的筛选操作。
- 通常根据行键和列来筛选数据

#### 基于行的过滤器

- prefixFilter：行前缀匹配
- pageFilter：基于行的分页

#### 基于列的过滤器

- ColumnPrefixFilter：列的前缀匹配
- FirstKeyOnlyFilter：只返回每一行的第一列

#### 基于单元格值的过滤器

- KeyOnlyFilter：返回的数据不包含单元值，只包含行键与列
- TimestampFilter：根据数据的时间戳版本进行过滤

#### 基于列和单元值的过滤器

- singleColumnValueFilter：对该列的单元值进行比较过滤
- singleColumnValueExcludeFilter：对该列的单元值进行比较过滤

#### 比较过滤器

- 比较过滤器通常需要一个比较运算符以及一个比较器来实现过滤
- RowFilter、FamilyFilter、qualifierFilter、ValueFilter



| 过滤器Filter       | 功能                           |
| ------------------ | ------------------------------ |
| RowFilter          | 筛选出匹配的行                 |
| PrefixFilter       | 筛选出具有特定前缀的行键的数据 |
| KeyOnlyFilter      | 只返回每行的行键，值全为空     |
| ColumnPrefixFilter | 按照列名的前缀来筛选单元格     |
| ValueFilter        | 按照具体的值来筛选单元格的过滤 |
| TimestampsFilter   | 根据数据的时间戳版本进行过滤   |
| FilterList         | 用于综合使用多个过滤器         |



#### 自定义过滤器

不推荐

### 

