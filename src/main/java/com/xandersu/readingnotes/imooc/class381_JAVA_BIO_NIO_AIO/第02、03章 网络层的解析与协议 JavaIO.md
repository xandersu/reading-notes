# 理论知识 网络层的解析与协议

##URL

http://www.google:80/search?!=test&safe=strict

协议    域名/IP地址    端口    路径    参数

## DNS解析过程

域名    =>    IP地址

根域名            .root

顶级域名         .com     .edu      .org

次级域名          .google           .mit         

主机名               www



DNS  domain  name   system

### DNS递归查询

浏览器 =>  DNS客户端  =>  根域名 =>    顶级域名  =>  二级域名 =>    三级域名

### DNS迭代查询

浏览器 =>  DNS客户端

  =>  根域名

 =>    顶级域名

  =>  二级域名

 =>    三级域名



## 网络协议

应用层		HTTP  FTP   SMTP

传输层		TCP   UDP

网络层		IP

链路层		Ethernet						帧

实体层

## 链路层数据包格式

帧 最大1518个字节										

Ethernet标头

Ethernet数据

IP数据



## 网路编程的本质是进程间通信

浏览器 <-> 服务器

## 通信的基础是IO模型

数据源	--输入流-->	应用

## Java IO家族

​								IO流 

​		字符流										字节流

Reader		Writer		InputStream		OutputStream

## Java字符流

​															字符流

​								Reader													Writer

CharArrayReader		StringReader		CharArrayWriter		StringWriter

高级字符流

BufferReader

FilterReader

InputStreamReader		(FileReader)

BufferWriter

FilterWriter

OutputStreamWriter		FileWriter

## Java字节流

字节流

InputStream		OutputStream

ByteArrayInputStream		FileInputStream		ByteArrayOutputStream		FileOutputStream

高级字节流

BufferedInputStream

DataInputStream

BufferedOutputStream

DataOutputStream



# Socket概述

- Unix系统中一切都是文件
- 文件描述符是已打开文件的索引
- 每个进程都会维护一个文件描述表



# 同步、异步、阻塞、非阻塞

| x        | x      | 通信机制   | 通信机制   |
| -------- | ------ | ---------- | ---------- |
| x        | x      | 同步       | 异步       |
| 调用状态 | 阻塞   | 同步阻塞   | 异步阻塞   |
| 调用状态 | 非阻塞 | 同步非阻塞 | 异步非阻塞 |

# 第四章 Socket和ServerSocket 

bind

accept      connect

I/O         I/O

close       close


# BIO编程模型

