# 第4章 HBase进阶

### 什么导致HBase性能下降

- JVM内存分配和GC回收策略
- 与HBase运行机制相关的 部分配置不合理
- 表结构设计及用户使用方式不合理

### HBase概念

- minor compaction:选取小的、相邻的storeFile将他们合并成一个更大的StoreFile
- major compaction：将所有的StoreFile合并成一个StoreFile，清理无意义数据：被删除的数据、TTL过期数据、版本号超过设定版本号的数据
- split：当一个region达到一定的大小就会自动split成两个region



### HBase Compact检查

- MemStore呗flush到磁盘
- 用户执行shell命令compact、major_compact或者调用了相应的api
- HBase后台线程周期性触发检查



### Hbase优化策略

- 常见的服务端配置优化
- 常见的优化策略（以实际需求为主）
- HBase读写性能优化



#### Hbase服务端配置优化策略

- JVM设置与GC设置
- hbase-site.xml

| HBase.properties                        | 简介                                                         |
| --------------------------------------- | ------------------------------------------------------------ |
| hbase.regionserver.handler.count        | rpc请求的线程数量，默认值是10                                |
| hbase.hregion.max.filesize              | 当region的大小大于设定值后hbase就会开始split                 |
| hbase.hregion.majorcompaction           | major compaction的执行周期                                   |
| hbase.hstore.compaction.min             | 一个store里storeFile总数超过该值，会触发默认的合并操作       |
| hbase.hstore.compaction.max             | 一次最多合并多少个storefile                                  |
| habse.hstore.blockingStoreFiles         | 一个region中的Store（Column Family）内有超过xx个storefile事，则block所有写请求进行compaction |
| hfile.block.cache.size                  | region server放入block dize的内存大小限制                    |
| hbase.hregion.memstore.flush.size       | memStore超过该值将被flush                                    |
| hbase.hregion.memstore.block.multiplier | 如果memStore的内存超过flush.size * multiplier，会阻塞改memstore的写操作 |



### HBase常用优化

- 预先分区
- RowKey优化
- Column优化
- Schema优化

### 预先分区

- 创建HBase表时会自动创建一个region分区
- 创建HBase表时预先创建一些空regions

### RowKey优化

- 利用HBase默认排序的特点，讲义气访问的数据放到一起
- 防止热点问题，避免使用时序或者单调的递增递减。
- 加盐、随机数、hash、反转、长度尽可能短

### Column优化

- 列族的名称和列的描述命名尽量简短
- 同一张表中COlumnFamily的数量不超过3个

### Schema优化

- 宽表：一种列多行少的设计
- 高表：一种列少行多的设计

### HBase写优化策略

- 同步批量提交 or 异步批量提交
- WAL优化，是否必须，持久化等级



### HBase读优化策略

- 客户端：Scan缓存设置，批量获取
- 服务端：BlockSize配置是否合理，HFile是否过多
- 表结构设计问题



# HBase 协处理器 Coprocessor

//TODO