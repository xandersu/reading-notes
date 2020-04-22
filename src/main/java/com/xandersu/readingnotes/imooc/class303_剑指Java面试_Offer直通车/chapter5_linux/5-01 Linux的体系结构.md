# 5-01 Linux的体系结构

#Linux

应用程序-shell/公共函数库-系统调用-内核

- 体系结构主要分为用户态（用户上层活动）和内核态
  - linux启动时首先启动内核knernal
- 内核：本质是一段管理计算机硬件设备的程序
  - 内核直接管理硬件，包括CPU、内存空间、硬盘接口、网络接口等等，所有的计算机操作都要通过内核传递给硬件，用户态及上层应用程序的活动空间、应用程序的执行必须依托于内核提供的资源，包括CPU资源、存储资源、IO资源等，为了使上层应用访问到这些资源，内核必须为上层应用提供访问的接口，即系统调用。
- 系统调用：内核的访问接口，是一种不能再简化的操作.(man 2查看系统调用)
  - man 2 syscalls
  - man 2 acct
  - man 2：代表系统调用；3：公共库函数。
  - 一个操作系统上的功能可以看做是系统调用的组合的效果，而且一个操作系统不可能做出超越系统调用的动作，比如，给某个内存变量分配内存空间，就必须调用很多系统调用。
- 公共函数库：系统调用的组合拳
  - 将程序员从复杂的系统调用解脱，实现系统调用的封装，实现简单的业务逻辑接口呈现给用户，方便调用。
  - 有不同的标准不同的实现版本：iso-c标准库、....标准库
- Shell：命令解释器，可编程
  - 充当胶水，连接各个功能程序，让不同程序以一个清晰的接口协同工作，增强各个程序的功能。
  - ls -lrt 
  - 没用图形界面之前，充当用户界面，当用户需要运行某些应用时，要通过shell输入命令，以建立运行程序。
  - shell可编程，可执行符合shell语法的文本，这样的文本称为shell脚本。shell脚本对系统调用进行封装。

ls 查询目录下的文件

cat 查看文件内容

less 查看文件内容

more 查看内容

vi 编辑文件，对文件进行增删改查

vim 编辑文件



# 如何查找特定文件

## find

- 语法：find path [options] params

- 作用：在指定目录下查找文件

- find ~ -name "target3.java" 精确查找

- find / -name "target3.java" 到根目录下全局

- find ~ -name "target*" 模糊查询

- find ~ -iname "target*" 加i忽略大小写
- man find 更多关于find指令的使用说明



# 检索文件内容

## grep

语法：grep [options] pattern file

全称：global regular expression print

作用：查找文件里符合条件的字符串

grep "moo" target* 在target开头的文件内找到moo文字

指令里不指定任何文件名称，grep会从标准输入设备读取设备



## 管道操作符 |

- 可将指令连接起来，前一个指令的输出作为后一个指令的输入

command1 | command2 | command3

- 只能处理前一个命令正确的输出，不处理错误的输出
- 右边命令必须能够接手标准输入流，否则传递过程中数据会被抛弃
- sed,awk,grep,cut,head,top,less,more,wc,join,sort,split



`grep 'partial\[\]'(特殊符号转义) xxx.log`  查找包含的字符

`grep 'partial\[\]'(特殊符号转义) xxx.log |grep -o 'engine\[[0-9a-z]*\]' `查找包含正则表达式

greo -o 'engine[[0-9a-z]]' 筛选出符合正则表达式的内容

grep -v 'grep'过滤

ps -ef | grep tomcat | grep -v "grep"



# 对文件内容统计

## awk

语法： awk [options] 'cmd' file

- 一次读取一行文本，按输入分隔符进行切片，切成多个组成部分
- 将切片直接保存在内建的变量中， `$1,$2...($0表示行的全部)`
- 支持对单个切片的判断，支持循环判断，默认分隔符为空空格

`awk '{print $1,$4}' xxx.txt` 打印出第一列，第四列

`awk '$1=="tcp" && $2==1{print $0}' xxx.txt` 满足条件的

`awk '()$1=="tcp" && $2==1)||NR=1 {print $0}' xxx.txt` 带第一行并满足条件

`awk -F "," '{print $0}' xxx.txt`  -F以，为分隔符

`awk '{print $1,$4}' xxx.txt `筛选文件

`awk '$1=="tcp" && $2==1{print $0}' xxx.txt `    按条件筛选文件

`awk '{enginearr[$1]++}END{for(i in enginearr)print i "\t" enginearr[i]}'` 统计



# 批量替换文本内容

## sed

语法： sed [option] 'sed command' filename

全名：stream editor,流编辑器

- 适合用于对于文本的行内容进行处理
- 依据规则删除某行

 `sed 's/^Str/String/' xxx.txt` s字符串操作,-i修改文件  `sed -i 's/^Str/String/' xxx.txt` `sed -i 's/\.$/\;/' xxx.txt` `sed -i 's/\.$/\;/g' xxx.txt` 没有g替换一行中首次出现的，加g表示替换一行中所有 

 `sed -i '/^ *$/d' xxx.txt` d代表删除行

 `sed -i '/Integer/d' xxx.txt` 删除Integer所在的行