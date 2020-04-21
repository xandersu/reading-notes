# 4-08 Pipeline及主从同步

## 批量生成redis测试数据

1. Linux Bash下执行：for(i=1;i<=2000000;i++); do echo "set k`$`i  v`$`i"" >> /temp/redisTest.txt ; done;生成数据。
2. 用vim去掉行尾的^M符号：vim /tmp/redis.txt :set fileformat=dos #设置文件的格式，通过这句话去掉每行结尾的^M符号 :wq退出
3. 通过redis提供的管道 --pipe形式，跑redis，传入指令灌数据：cat /tmp/redisTest.txt | 路径/redis-5.0.0/src/redis-cli -h 主机ip -p 端口号 --pipe

# 使用pipeline的好处

- pipeline和linux的管道类似
- Redis基于请求/相应模型，单个请求处理需一一应答
- pipeline批量执行指令，节约多次IO往返的时间
- 有顺序依赖的指令建议分批发送

# Redis的同步机制

### 主从同步原理

### 全同步流程

- salve发送sync命令道master
- master启动一个后台进程，将redis中的数据快照保存到文件中
- master将保存数据快照期间接收到的写命令缓存起来
- master完成写文件操作后，将该文件发送给salve
- Slave使用新的AOF文件替换掉旧的AOF文件
- Master将这期间收集的增量写命令发送给salve端

### 增量同步过程

- master接收到用户的操作命令，判断是否需要传播到slave
- 将操作记录追加到aof文件
- 将操作传播到其他slave，1、对齐主从库；2、往相应缓存写入指令
- 将缓存中的数据发送给slave

# Redis Sentinel

### 解决主从同步Master宕机后的主从切换问题：

- 监控： 检查主从服务器是否运行正常
- 提醒： 通过API向管理员或者其他应用程序发送故障通知
- 自动故障迁移：主从切换

# 流言协议Gossip  反熵

### 在杂乱无章中寻求一致

- 每个节点都随机地与对方通信，最终所有节点的状态达成一致
- 种子节点定期随机向其他节点发送节点列表以及需要传播的消息
- 不保证信息一定会传递给所有节点，但是最终会趋于一致