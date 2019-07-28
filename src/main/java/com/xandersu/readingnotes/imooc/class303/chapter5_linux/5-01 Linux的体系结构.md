# 5-01 Linux的体系结构

#Linux

应用程序-shell/公共函数库-系统调用-内核

- 体系结构主要分为用户态（用户上层活动）和内核态
- 内核：本质是一段管理计算机硬件设备的程序
- 系统调用：内核的访问接口，是一种不能再简化的操作.(man 2查看系统调用)
  - man 2 syscalls
  - man 2 acct
- 公共函数库：系统调用的组合拳
- Shell：命令解释器，可编程



# 如何查找特定文件

## find

语法：find path [options] params

作用：在指定目录下查找文件

find ~ -name "target3.java" 精确查找

find / -name "target3.java" 到根目录下全局

find ~ -name "target*" 模糊查询

find ~ -iname "target*" 加i忽略大小写



# 检索文件内容

## grep

语法：grep [options] pattern file

全称：global regular expression print

作用：查找文件里符合条件的字符串

## 管道操作符 |

- 可将指令连接起来，前一个指令的输出作为后一个指令的输入

command1 | command2 | command3

- 只能处理前一个命令正确的输出，不处理错误的输出
- 右边命令必须能够接手标准输入流，否则传递过程中数据会被抛弃
- sed,awk,grep,cut,head,top,less,more,wc,join,sort,split



`grep 'partial\[\]'(特殊符号转义) xxx.log`  查找包含的字符

`grep 'partial\[\]'(特殊符号转义) xxx.log |grep -o 'engine\[[0-9a-z]*\]' `查找包含正则表达式

grep -v 'grep'过滤



# 对文件内容统计

## awk

语法： awk [options] 'cmd' file

- 一次读取一行文本，按输入分隔符进行切片，切成多个组成部分
- 将切片直接保存在内建的变量中， `$1,$2...(0表示行的全部)`
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