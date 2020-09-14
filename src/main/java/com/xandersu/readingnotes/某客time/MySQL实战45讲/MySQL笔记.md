
# 16. orderBy是怎么工作的？

1. 内存sort_buffer够的情况下，将数据取出来放到内存中，对排序列做快速排序。
2. 内存sort_buffer不够的情况下，在磁盘上创建临时文件，将数据取出来分到12个文件上，每一份单独排序，在把12份有序的数据合并到一个有序的大文件。
3. 如果排序的单行数据量太大，会将id和排序列取出来，进行快排，然后通过id回表查出来需要的数据。
4. 如果有覆盖索引则无需回表并且索引的值是有序的。



# 18. SQL语句逻辑相同，性能差异大？

1. 不要在where后的索引字段上加函数，会破坏索引的有序性，导致放弃树的搜索能力。
2. 注意隐式的类型转换，=号左边的是字段是varchar，=号右边的是数字。
   1. mysql里字符串和数字比较，会把字符串转换为数字，其实就是在字段上加了cast函数。
3. 一个是 utf8，一个是 utf8mb4，关联查询时会将utf8转换成utf8mb4，也相当于加了个函数。
   1. 优化方式：全转成utf8mb4
   2. SQL等号右边主动转

对索引字段做函数操作，可能会破坏索引值的有序性，因此优化器就决定放弃走树搜索功能。



# 19 | 为什么我只查一行的语句，也执行这么慢？

1. 表锁，MDL 锁
2. 行锁
3. flush数据
4. 事务太大，undo log很长，老事务从undo log链上找到符合他的可见性的数据要从尾到头进行遍历。

# 20 | 幻读是什么，幻读有什么问题？

间隙锁之间都不存在冲突关系。跟间隙锁存在冲突关系的，是“**往这个间隙中插入一个记录**”这个操作。

间隙锁和行锁合称 next-key lock，每个 next-key lock 是前开后闭区间。也就是说，我们的表 t 初始化以后，如果用 select * from t for update 要把整个表所有记录锁起来，就形成了 7 个 next-key lock，分别是 (-∞,0]、(0,5]、(5,10]、(10,15]、(15,20]、(20, 25]、(25, +supremum]。



## 21 | 为什么我只改一行的语句，锁这么多？



两个“原则”、两个“优化”和一个“bug”。

1. 原则 1：加锁的基本单位是 next-key lock。希望你还记得，next-key lock 是前开后闭区间。
2. 原则 2：查找过程中访问到的对象才会加锁。
3. 优化 1：索引上的等值查询，给唯一索引加锁的时候，next-key lock 退化为行锁。
4. 优化 2：索引上的等值查询，向右遍历时且最后一个值不满足等值条件的时候，next-key lock 退化为间隙锁。
5. 一个 bug：唯一索引上的范围查询会访问到不满足条件的第一个值为止。



在删除数据的时候尽量加 limit。这样不仅可以控制删除数据的条数，让操作更安全，还可以减小加锁的范围。



# 22 | MySQL有哪些“饮鸩止渴”提高性能的方法？

超过max_connections 参数值，系统就会拒绝接下来的连接请求，并报错提示“Too many connections”。

处理掉那些占着连接但是不工作的线程。

- 杀掉空闲太久的连接。

减少连接过程的消耗。

- 让数据库跳过权限验证阶段：重启数据库，并使用–skip-grant-tables 参数启动。



慢查询性能问题

1. 索引没有设计好；
   set sql_log_bin=off 不写binlog。

2. SQL 语句没写好；
   查询重写：
   mysql> insert into query_rewrite.rewrite_rules(pattern, replacement, pattern_database) values ("select * from t where id + 1 = ?", "select * from t where id = ? - 1", "db1");

   call query_rewrite.flush_rewrite_rules();

3. MySQL 选错了索引。
   force index。



索引没设计好和语句没写好：

1. 上线前，在测试环境，把慢查询日志（slow log）打开，并且把 long_query_time 设置成 0，确保每个语句都会被记录入慢查询日志；
2. 在测试表里插入模拟线上的数据，做一遍回归测试；
3. 观察慢查询日志里每类语句的输出，特别留意 Rows_examined 字段是否与预期一致。



# 23 | MySQL是怎么保证数据不丢的？

### binlog 的写入机制

事务执行过程中，先把日志写到 binlog cache，事务提交的时候，再把 binlog cache 写到 binlog 文件中。

write 和 fsync 的时机，是由参数 sync_binlog 控制的：

1. sync_binlog=0 的时候，表示每次提交事务都只 write，不 fsync；
2. sync_binlog=1 的时候，表示每次提交事务都会执行 fsync；
3. sync_binlog=N(N>1) 的时候，表示每次提交事务都 write，但累积 N 个事务后才 fsync。

## redo log 的写入机制

控制 redo log 的写入策略，InnoDB 提供了 innodb_flush_log_at_trx_commit 参数

1. 设置为 0 的时候，表示每次事务提交时都只是把 redo log 留在 redo log buffer 中 ;
2. 设置为 1 的时候，表示每次事务提交时都将 redo log 直接持久化到磁盘；
3. 设置为 2 的时候，表示每次事务提交时都只是把 redo log 写到 page cache。

InnoDB 有一个后台线程，每隔 1 秒，就会把 redo log buffer 中的日志，调用 write 写到文件系统的 page cache，然后调用 fsync 持久化到磁盘。

1. 一种是，redo log buffer 占用的空间即将达到 innodb_log_buffer_size 一半的时候，后台线程会主动写盘。
2. 另一种是，并行的事务提交的时候，顺带将这个事务的 redo log buffer 持久化到磁盘。

时序上 redo log 先 prepare， 再写 binlog，最后再把 redo log commit。



# 24 | MySQL是怎么保证主备一致的？

1. 在备库 B 上通过 change master 命令，设置主库 A 的 IP、端口、用户名、密码，以及要从哪个位置开始请求 binlog，这个位置包含文件名和日志偏移量。
2. 在备库 B 上执行 start slave 命令，这时候备库会启动两个线程，就是图中的 io_thread 和 sql_thread。其中 io_thread 负责与主库建立连接。
3. 主库 A 校验完用户名、密码后，开始按照备库 B 传过来的位置，从本地读取 binlog，发给 B。
4. 备库 B 拿到 binlog 后，写到本地文件，称为中转日志（relay log）。
5. sql_thread 读取中转日志，解析出日志里的命令，并执行。

### binlog 的三种格式

一种是 statement，一种是 row，第三种格式，叫作 mixed，它是前两种格式的混合。

1. statement：SQL原文
2. row：Table_map event ；Delete_rows event；binlog 里面记录了真实删除行的主键 id，
3. 判断这条 SQL 语句是否可能引起主备不一致，如果有可能，就用 row 格式，否则就用 statement 格式。

### 为什么会有 mixed 这种 binlog 格式的存在场景？

- 因为有些 statement 格式的 binlog 可能会导致主备不一致，所以要使用 row 格式。
- 但 row 格式的缺点是，很占空间。statement 的话就是一个 SQL 语句被记录到 binlog 中。row 格式的 binlog，就要把这 10 万条记录都写到 binlog 中。

#### binlog 格式设置成 row作用：恢复数据



# 25 | MySQL是怎么保证高可用的？


备库上执行 show slave status 命令，它的返回结果里面会显示 seconds_behind_master，用于表示当前备库延迟了多少秒。

1. 每个事务的 binlog 里面都有一个时间字段，用于记录主库上写入的时间；
2. 备库取出当前正在执行的事务的时间字段的值，计算它与当前系统时间的差值，得到 seconds_behind_master。

备库连接到主库的时候，会通过执行 SELECT UNIX_TIMESTAMP() 函数来获得当前主库的系统时间。不一致，备库在执行 seconds_behind_master 计算的时候会自动扣掉这个差值。

主备延迟最直接的表现是，备库消费中转日志（relay log）的速度，比主库生产 binlog 的速度要慢。

### 主备延迟的来源

- 首先，有些部署条件下，备库所在机器的性能要比主库所在的机器性能差。
- 备库的压力大。
  - 一主多从
  - 通过 binlog 输出到外部系统，比如 Hadoop 这类系统，让外部系统提供统计类查询的能力。
- 大事务
  - 一次性地用 delete 语句删除太多数据
  - 大表 DDL
- 备库的并行复制能力

### 可靠性优先策略

[![wDCain.png](https://s1.ax1x.com/2020/09/14/wDCain.png)](https://imgchr.com/i/wDCain)

1. 判断备库 B 现在的 seconds_behind_master，如果小于某个值（比如 5 秒）继续下一步，否则持续重试这一步；
2. 把主库 A 改成只读状态，即把 readonly 设置为 true；
3. 判断备库 B 的 seconds_behind_master 的值，直到这个值变成 0 为止；
4. 把备库 B 改成可读写状态，也就是把 readonly 设置为 false；
5. 把业务请求切到备库 B。

### 可用性优先策略

不等主备数据同步，直接把连接切到备库 B，并且让备库 B 可以读写，那么系统几乎就没有不可用时间了。



# 26 | 备库为什么会延迟好几个小时？

coordinator 在分发的时，满足两个要求

1. 不能造成更新覆盖。这就要求更新同一行的两个事务，必须被分发到同一个 worker 中。
2. 同一个事务不能被拆开，必须放到同一个 worker 中。



# 27 | 主库出问题了，从库怎么办？

当我们把节点 B 设置成节点 A’的从库的时候，需要执行一条 change master 命令：

CHANGE MASTER TO 

MASTER_HOST=$host_name 

MASTER_PORT=$port 

MASTER_USER=$user_name 

MASTER_PASSWORD=$password 

MASTER_LOG_FILE=$master_log_name 

MASTER_LOG_POS=$master_log_pos  

- MASTER_HOST、MASTER_PORT、MASTER_USER 和 MASTER_PASSWORD 四个参数，分别代表了主库 A’的 IP、端口、用户名和密码。
- 最后两个参数 MASTER_LOG_FILE 和 MASTER_LOG_POS 表示，要从主库的 master_log_name 文件的 master_log_pos 这个位置的日志继续同步。而这个位置就是我们所说的同步位点，也就是主库对应的文件名和日志偏移量。

一种取同步位点的方法是这样的：

- 等待新主库 A’把中转日志（relay log）全部同步完成；
- 在 A’上执行 show master status 命令，得到当前 A’上最新的 File 和 Position；
- 取原主库 A 故障的时刻 T；
- 用 mysqlbinlog 工具解析 A’的 File，得到 T 时刻的位点。

mysqlbinlog File --stop-datetime=T --start-datetime=T



通常情况下，我们在切换任务的时候，要先主动跳过这些错误，有两种常用的方法。

一种做法是，主动跳过一个事务。跳过命令的写法是：

set global sql_slave_skip_counter=1;start slave;

另外一种方式是，通过设置 slave_skip_errors 参数，直接设置跳过指定的错误。在执行主备切换时，有这么两类错误，是经常会遇到的：1062 错误是插入数据时唯一键冲突；1032 错误是删除数据时找不到行。因此，我们可以把 slave_skip_errors 设置为 “1032,1062”。

## GTID

Global Transaction Identifier，也就是全局事务 ID，是一个事务在提交的时候生成的，是这个事务的唯一标识。

它由两部分组成，格式是：

GTID=server_uuid:gno

其中：server_uuid 是一个实例第一次启动时自动生成的，是一个全局唯一的值；

gno 是一个整数，初始值是 1，每次提交事务的时候分配给这个事务，并加 1。



在 MySQL 的官方文档里，GTID 格式是这么定义的：GTID=source_id:transaction_id

在 MySQL 里面我们说 transaction_id 就是指事务 id，事务 id 是在事务执行过程中分配的，如果这个事务回滚了，事务 id 也会递增，而 gno 是在事务提交的时候才会分配。

GTID 有两种生成方式，而使用哪种方式取决于 session 变量 gtid_next 的值。

1. 如果 gtid_next=automatic，代表使用默认值。这时，MySQL 就会把 server_uuid:gno 分配给这个事务。a. 记录 binlog 的时候，先记录一行 SET @@SESSION.GTID_NEXT=‘server_uuid:gno’;b. 把这个 GTID 加入本实例的 GTID 集合。
2. 如果 gtid_next 是一个指定的 GTID 的值，比如通过 set gtid_next='current_gtid’指定为 current_gtid，那么就有两种可能：
   1. a. 如果 current_gtid 已经存在于实例的 GTID 集合中，接下来执行的这个事务会直接被系统忽略；
   2. b. 如果 current_gtid 没有存在于实例的 GTID 集合中，就将这个 current_gtid 分配给接下来要执行的事务，也就是说系统不需要给这个事务生成新的 GTID，因此 gno 也不用加 1。



# 28 | 读写分离有哪些坑？

由于主从可能存在延迟，客户端执行完一个更新事务后马上发起查询，如果查询选择的是从库的话，就有可能读到刚刚的事务更新之前的状态。

- 强制走主库方案；
- sleep 方案；
- 判断主备无延迟方案；
- 配合 semi-sync 方案；
- 等主库位点方案；
- 等 GTID 方案。

## 强制走主库方案

将查询请求做分类

1. 对于必须要拿到最新结果的请求，强制将其发到主库上。
2. 对于可以读到旧数据的请求，才将其发到从库上。

## Sleep 方案

主库更新后，读从库之前先 sleep 一下。具体的方案就是，类似于执行一条 select sleep(1) 命令。

大多数情况下主备延迟在 1 秒之内，做一个 sleep 可以有很大概率拿到最新的数据。

## 判断主备无延迟方案

第一种确保主备无延迟的方法是，每次从库执行查询请求前，先判断 seconds_behind_master 是否已经等于 0。如果还不等于 0 ，那就必须等到这个参数变为 0 才能执行查询请求。

seconds_behind_master 的单位是秒

第二种方法，对比位点确保主备无延迟：

- Master_Log_File 和 Read_Master_Log_Pos，表示的是读到的主库的最新位点；
- Relay_Master_Log_File 和 Exec_Master_Log_Pos，表示的是备库执行的最新位点。

如果 Master_Log_File 和 Relay_Master_Log_File、Read_Master_Log_Pos 和 Exec_Master_Log_Pos 这两组值完全相同，就表示接收到的日志已经同步完成。

第三种方法，对比 GTID 集合确保主备无延迟：

- Auto_Position=1 ，表示这对主备关系使用了 GTID 协议。
- Retrieved_Gtid_Set，是备库收到的所有日志的 GTID 集合；
- Executed_Gtid_Set，是备库所有已经执行完成的 GTID 集合。

如果这两个集合相同，也表示备库接收到的日志都已经同步完成。

可能出现的问题，主库的binlog还没有发给从库，客户端收到主库的事务提交信息，这时从库认为没有主从延迟，但实际是有问题的。

## 配合 semi-sync

半同步复制，也就是 semi-sync replication。

semi-sync 做了这样的设计：

- 事务提交的时候，主库把 binlog 发给从库；
- 从库收到 binlog 以后，发回给主库一个 ack，表示收到了；
- 主库收到这个 ack 以后，才能给客户端返回“事务完成”的确认。

如果启用了 semi-sync，就表示所有给客户端发送过确认的事务，都确保了备库已经收到了这个日志。

semi-sync 配合判断主备无延迟的方案，存在两个问题：

1. 一主多从的时候，在某些从库执行查询请求会存在过期读的现象；
2. 在持续延迟的情况下，可能出现过度等待的问题。

## 等主库位点方案

select master_pos_wait(file, pos[, timeout]);

- 它是在从库执行的；
- 参数 file 和 pos 指的是主库上的文件名和位置；
- timeout 可选，设置为正整数 N 表示这个函数最多等待 N 秒。

这个命令正常返回的结果是一个正整数 M，表示从命令开始执行，到应用完 file 和 pos 表示的 binlog 位置，执行了多少事务。

- 如果执行期间，备库同步线程发生异常，则返回 NULL；
- 如果等待超过 N 秒，就返回 -1；
- 如果刚开始执行的时候，就发现已经执行过这个位置了，则返回 0。

要保证能够查到正确的数据，我们可以使用这个逻辑：

- trx1 事务更新完成后，马上执行 show master status 得到当前主库执行到的 File 和 Position；
- 选定一个从库执行查询语句；在从库上执行 select master_pos_wait(File, Position, 1)；
- 如果返回值是 >=0 的正整数，则在这个从库执行查询语句；
- 否则，到主库执行查询语句。

## GTID 方案

 select wait_for_executed_gtid_set(gtid_set, 1);

- 等待，直到这个库执行的事务中包含传入的 gtid_set，返回 0；
- 超时返回 1。

MySQL 5.7.6 版本开始，允许在执行完更新类事务后，把这个事务的 GTID 返回给客户端，这样等 GTID 的方案就可以减少一次查询。

等 GTID 的执行流程就变成了：

- trx1 事务更新完成后，从返回包直接获取这个事务的 GTID，记为 gtid1；
- 选定一个从库执行查询语句；
- 在从库上执行 select wait_for_executed_gtid_set(gtid1, 1)；
- 如果返回值是 0，则在这个从库执行查询语句；
- 否则，到主库执行查询语句。

参数 session_track_gtids 设置为 OWN_GTID，然后通过 API 接口 mysql_session_track_get_first 从返回包解析出 GTID 的值即可。



# 29 | 如何判断一个数据库是不是出问题了？

## select 1 判断

select 1 成功返回，只能说明这个库的进程还在，并不能说明主库没问题

innodb_thread_concurrency 这个参数的默认值是 0，表示不限制并发线程数量。但是，不限制并发线程数肯定是不行的。

因为，一个机器的 CPU 核数有限，线程全冲进来，上下文切换的成本就会太高。

所以，通常情况下，我们建议把 innodb_thread_concurrency 设置为 64~128 之间的值。

**并发连接**和**并发查询**，并不是同一个概念。

你在 show processlist 的结果里，看到的几千个连接，指的就是并发连接。

而“当前正在执行”的语句，才是我们所说的并发查询。

**在线程进入锁等待以后，并发线程的计数会减一**，也就是说等行锁（也包括间隙锁）的线程是不算在 128 里面的。

## 查表判断

为了能够检测 InnoDB 并发线程数过多导致的系统不可用情况

在系统库（mysql 库）里创建一个表，比如命名为 health_check，里面只放一行数据，然后定期执行：

mysql> select * from mysql.health_check;

空间满了以后，这种方法又会变得不好使。一旦 binlog 所在磁盘的空间占用率达到 100%，那么所有的更新语句和事务提交的 commit 语句就都会被堵住。但是，系统这时候还是可以正常读数据的。

## 更新判断

放个有意义的字段，常见做法是放一个 timestamp 字段，用来表示最后一次执行检测的时间。这条更新语句类似于：

mysql> update mysql.health_check set t_modified=now();

外部检测都需要定时轮询，所以系统可能已经出问题了，但是却需要等到下一个检测发起执行语句的时候，我们才有可能发现问题。导致切换慢的问题。

## 内部统计

MySQL 5.6 版本以后提供的 performance_schema 库，就在 file_summary_by_event_name 表里统计了每次 IO 请求的时间。



什么时候会把线上生产库设置成“非双 1”。

业务高峰期。一般如果有预知的高峰期，DBA 会有预案，把主库设置成“非双 1”。

备库延迟，为了让备库尽快赶上主库。

用备份恢复主库的副本，应用 binlog 的过程，这个跟上一种场景类似。

批量导入数据的时候。


## 31 | 误删数据后除了跑路，还能怎么办？

取最近一次全量备份，假设这个库是一天一备，上次备份是当天 0 点；用备份恢复出一个临时库；从日志备份里面，取出凌晨 0 点之后的日志；把这些日志，除了误删除数据的语句外，全部应用到临时库。