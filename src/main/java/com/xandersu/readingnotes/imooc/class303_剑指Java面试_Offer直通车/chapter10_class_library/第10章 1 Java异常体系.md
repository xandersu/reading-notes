# 第10章 1 Java常用类库与技巧

## Java异常

异常处理机制主要回答了三个问题

- what：异常类型回答了什么被抛出
- where：异常堆栈跟踪回答了在哪被抛出
- why：异常信息回答了为什么被抛出



## Error和exception的区别

### 从程序角度解析Java的异常处理机制

- Error：程序无法处理的系统错误，编译器不做检查

  - 如JVM崩溃，虚拟机栈溢出、内存空间不足
  - 对于这类错误导致的崩溃，系统无法恢复和预防

- Exeption：程序可以处理的异常，捕获后可能恢复

- 总结：前者是程序无法处理的错误，后者是可以处理的异常



### Java异常体系

Throwable

Error

exception：RuntimeException、非RuntimeException

- RuntimeException：不可预知的，程序应当自行避免
- 非RuntimeException：可预知的，从编译器校验的异常



### 从责任角度看

1. Error属于JVM需要负担的责任；
2. RuntimeException是程序应当负担的责任；
3. checked Exception可检查异常是Java编译器应该负的责任。

### 常见的Error以及Exception

#### RuntimeException

1. NullPointerException - 空指针异常
2. ClassCastException - 类型强制转换异常
3. IllegalArgumentException - 传递非法参数异常
4. IndexOutOfBoundsException - 下标越界异常
5. NumberFormatException - 数字格式异常

#### 非RuntimeException

1. ClassNotFoundException - 找不到指定class的异常
2. IOException - IO操作异常

#### Error

1. NoClassDefFoundError - 找不到class定义异常
   1. 类依赖的class或者jar不存在
   2. 类文件存在，但是存在不同的域中
   3. 大小写问题，javac编译的时候无视大小写，很有可能编译出来的class文件和想要的不一样
2. stackPverflowError - 深递归导致栈被耗尽而抛出的异常
3. OutOfMemoryError - 内存溢出异常



## Java的异常处理机制

- 抛出异常：创建异常对象，交由运行时系统处理
- 捕获异常：寻找合适的异常处理器处理异常，否则终止运行



## Java的异常处理原则

- 具体明确：抛出的异常应能通过异常类名和message准确说明异常的类型和产生异常的原因。
- 提早抛出：应尽可能早的发现并抛出异常，便于精确定位问题；
- 延迟捕捉：异常的捕捉和处理应该尽可能延迟，让掌握更多信息的作用域来处理异常；



## 高效主流的异常处理框架

在用户看来，应用系统发生的所有异常都是应用程序的异常

- 设计一个通用的继承自RuntimeException的异常统一处理
- 其余异常都统一转义为上述异常AppException
- 在catch后，抛出上述异常的子类，并提供足以定位的信息



### Java异常处理消耗性能的地方

- try-catch 块影响JVM的优化
- 异常对象实例需要保存栈快照等信息，开销较大

















