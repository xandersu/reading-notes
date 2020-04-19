# 2-10 Socket相关

两个进程通信最基本的前提是唯一标识一个进程。

本地进程可以使用pid，pid只在本机唯一。

ip层的ip地址可以唯一标识一台主机，TCP协议和端口号可以唯一标识主机的一个进程，IP地址+TCP协议+端口号唯一标识网络中的一个进程。

## Socket简介
### Socket是对TCP/IP协议的抽象，是操作系统对外开放的接口。

### 使得程序员更方便的使用TCP/IP协议。

用户进程、用户进程  =>  应用层
            socket抽象层 

​       TCP 、  UDP   =>   运输层
ICMP  、  IP  、    IGMP   =>    网络层
ARP  、    硬件接口  、    RARP    =>   链路层

​           媒体

基本的函数接口create、listen、connect、accept、send、read、write等等。

socket起源于unix，一切皆文件，scoket基于打开、读和写、关闭的模式。服务器和客户端各自维护一个文件，在建立连接打开后，可以向自己的文件写入内容，供对方读取或者读取对方内容，在通信结束时就会关闭文件。

##Socket通信流程

### Socket通信流程：

​             			  server   

​                               |

创建socket        socket()                   						 		 client
                            |                       											 |
绑定socket和端口号    bind（）                  	  socket（）创建socket   
                        |                  											     |
监听该端口号          listen()                  			 connect（）连接指定计算机的端口
                        |              											         |
接收来之客户端的连接请求  accept（）        send（） 向socket中写入信息
                        |            												           |
从socket读取字符       recv（）                  	  close（）  关闭socket
                        |
关闭socket             close（）

服务创建socket，之后为socket绑定ip地址和端口号，服务器socket监听端口号的请求listen()，服务器socket接收到客户端的socket请求，被动打开，开始接受客户端的请求，直到客户端返回连接信息，服务器进入阻塞状态，accept方法需要一直等待客户端返回连接信息后才返回，同时接收下一个客户端的连接请求，服务端读取信息，服务端关闭socket

客户端创建socket，打开socket，根据服务器的ip地址和端口号尝试连接服务器的socket，客户端连接上服务端后，会向服务端发送连接信息，客户端向服务端发送数据，发送完消息后，客户端关闭socket。



## Socket相关面试题

编写一个应用程序，有客户端和服务端，客户端向服务端发送一个字符串，服务端收到字符串后将其打印到命令行上，然后向客户端返回该字符串长度，最后，客户端输出服务端返回的的该字符串长度，分别用TCP和UDP实现。