# 6-11 Java内存模型之 常考题解析

## JVM三大性能调调优参数 -Xms -Xmx -Xss的含义

`java -Xms 128m -Xmx 128m -Xss256k -jar xxx.jar`

- -Xss：规定了每个线程虚拟机栈（堆栈）的大小。
  - 一般256k足够了
  - 影响此进程中并发线程数的大小
- -Xms：堆的初始值
  - 一旦对象容量超过了初始容量，Java堆会自动扩容至-Xmx大小
- -Xmx：堆能达到的最大值

通常 -Xms -Xmx设置为一样大小，因为在扩容时会发生内存抖动影响程序运行的稳定性。

### java堆和栈的区别——内存分配策略

- 静态存储：编译时确定每个数据目标在运行时的存储空间需求
  - 这种策略要求程序代码中不允许有可变数据结构的存在，不允许有嵌套和递归的结构出现，会导致编译程序无法计算准确的存储空间
- 栈式存储：数据区需求在编译时未知，运行时模块入口前确定
  - 在运行中进入一个程序模块时，必须知道该程序模块所需要的数据区的大小才能分配内存
- 堆式存储：编译时或运行时模块入口无法确定，动态分配
  - 可变长度串、对象实例
  - 大片可利用块或空闲块组成，堆内内存可按照任意顺序分配和释放

### java内存模型中堆和栈的区别

联系：引用对象、数组时，栈里定义变量保存堆中目标的首地址

- 栈中的变量就成了数组或者对象的引用变量，就可以在程序中使用栈中的引用变量来访问堆中的数组或者对象
- 引用变量就是为堆中的数组或者对象起的名称
- 引用变量是普通的变量，定义时在栈中分配，
- 引用变量在程序运行到其作用域之外后就会被释放掉
- 数组和对象本身在堆中分配，即使程序运行到使用new产生数组或者对象的语句所在的代码块之外，数组和对象在堆中所占据的内存不会释放。
- 在没有引用对象指向后才会变成垃圾，会在随后的一个不确定的时间被垃圾回收器释放掉

1. 管理方式：栈自动释放，堆需要GC
2. 空间大小：栈比堆小
3. 碎片相关：栈产生的碎片远小于堆
4. 分配方式：栈支持静态和动态分配，堆仅支持动态分配
5. 效率：栈的效率比堆高



#### 元空间、堆、线程独占部分间的联系——内存角度

```
public class HelloWorld {
    private String name;
    public void sayHello(){
        System.out.println("Hello "+name );
    }
    
    public void setName(String name){
        this.name = name; 
    }

    public static void main(String[] args) {
        int a = 1;
        HelloWorld hw = new HelloWorld();
        hw.setName("test");
        hw.sayHello();
    }
}
```

- 元空间：
  - Class：Helloworld class对象信息
    - -Method:sayHello\setName\main
    - -field:name
  - Class:System类以及该类里的成员变量和方法
- Java堆：
  - Object：String("test")
  - Object：HelloWorld
- 线程独占：
  - Parameter refrence : "test" to String object ：test对应在堆中String对象的地址引用
  - Variable refrence : "hw" to HelloWorld object ： 本地变量hw对应堆中对象的地址引用
  - Local variables : a with 1 , line no ： 局部变量a保存的1，系统自带的行号



### String.intern()方法区别-jdk6 VS jdk6+

- JDK6:当调用intern方法时，如果字符串常量池先前已经创建出该字符串对象，则返回池中的该字符串的引用。否则将此字符串对象添加到字符串常量池中并且返回字符串对象的引用。

- JDK6+:当调用intern方法时，如果字符串常量池先前已经创建出该字符串对象，则返回池中的该字符串的引用。否则，如果该字符串对象已经存在与Java堆中，则将堆中对此对象的引用添加到字符串常量池中，并返回该引用； 如果堆中不存在，则在池中创建该字符串并返回其引用。



[![JschQI.png](https://s1.ax1x.com/2020/04/25/JschQI.png)](https://imgchr.com/i/JschQI)

s引用的是堆内的地址，s2引用的常量池的地址，所以==false

a在一开始的时候就放到常量池里了，s.intern()无法再放a到常量池

s3引用的是堆内的地址，s4引用的常量池的地址，所以==false



[![JsgQte.png](https://s1.ax1x.com/2020/04/25/JsgQte.png)](https://imgchr.com/i/JsgQte)

常量池已经存在a了，引用无法传递到常量值中的a

JDK6+ 可以把字符串的引用放到常量池中，JDK6之前只能放副本到常量池中

s引用的是堆内的地址，s2引用的常量池的地址，所以==false

a在一开始的时候就放到常量池里了，s.intern()无法再放a到常量池

s3引用的是堆内的地址，s4引用的常量池的地址，这个地址是堆中的引用，所以==true

aa没有放到常量池中，s3.intern()才把堆中的引用放到常量池中。



