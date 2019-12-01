# 2-10 Socket相关

## Socket简介
Socket是对TCP/IP协议的抽象，是操作系统对外开放的接口

应用层<br/>
|<br/>
socket抽象层 <br/>
|        |     |<br/>
|       TCP    UDP   运输层<br/>
ICMP    IP      IGMP   网络层<br/>
            |<br/>
ARP      硬件接口      RARP   链路层<br/>
            |<br/>
           媒体<br/>
                      
##Socket通信流程

Socket通信流程：

                    server   
                      |
创建socket        socket()                     client
                       |                        |
绑定socket和端口号    bind（）                    socket（）创建socket   
                        |                       |
监听该端口号          listen()                   connect（）连接指定计算机的端口
                        |                       |
接收来之客户端的连接请求  accept（）               send（） 向socket中写入信息
                        |                       |
从socket读取字符       recv（）                    close（）  关闭socket
                        |
关闭socket             close（）
