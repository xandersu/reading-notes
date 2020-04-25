package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

/**
 * @author: suxun
 * @date: 2020/4/25 13:54
 * @description: JDK6前后的String.intern()方法的不同，JDK6+ 可以把字符串的引用放到常量池中，JDK6之前只能放副本到常量池中
 * JDK6
 * s引用的是堆内的地址，s2引用的常量池的地址，所以==false
 * <p>
 * a在一开始的时候就放到常量池里了，s.intern()无法再放a到常量池
 * <p>
 * s3引用的是堆内的地址，s4引用的常量池的地址，所以==false
 * <p>
 * JDK6+
 * 常量池已经存在a了，引用无法传递到常量值中的a
 * <p>
 * <p>
 * s引用的是堆内的地址，s2引用的常量池的地址，所以==false
 * <p>
 * a在一开始的时候就放到常量池里了，s.intern()无法再放a到常量池
 * <p>
 * s3引用的是堆内的地址，s4引用的常量池的地址，这个地址是堆中的引用，所以==true
 * <p>
 * aa没有放到常量池中，s3.intern()才把堆中的引用放到常量池中。
 */
public class InternDifference {
    public static void main(String[] args) {
        String s = new String("a");
        s.intern();
        String s2 = "a";
        System.out.println(s == s2);

        String s3 = new String("a") + new String("a");
        s3.intern();
        String s4 = "aa";
        System.out.println(s3 == s4);
    }
}
