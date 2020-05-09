package com.xandersu.readingnotes.source;

import org.springframework.stereotype.Component;

/**
 * @author su
 * @date 2020/5/922:41
 * @description cglib 动态代理
 * 1.静态代理实现较简单，只要代理对象对目标对象进行包装，即可实现增强功能，但静态代理只能为一个目标对象服务，如果目标对象过多，则会产生很多代理类。
 * 2.JDK动态代理需要目标对象实现业务接口，代理类只需实现InvocationHandler接口。
 * 3.动态代理生成的类为 lass com.sun.proxy.$Proxy4，cglib代理生成的类为class com.cglib.UserDao$$EnhancerByCGLIB$$552188b6。
 * 4.静态代理在编译时产生class字节码文件，可以直接使用，效率高。
 * 5.动态代理必须实现InvocationHandler接口，通过反射代理方法，比较消耗系统性能，但可以减少代理类的数量，使用更灵活。
 * 6.cglib代理无需实现接口，通过生成类字节码实现代理，比反射稍快，不存在性能问题，但cglib会继承目标对象，需要重写方法，所以目标对象不能为final类。
 */
@Component
public final class TestFinalCglibDelegate {


}
