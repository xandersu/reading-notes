package com.xandersu.readingnotes.source;

import java.lang.reflect.Proxy;

/**
 * @author su
 * @date 2020/5/922:41
 * @description
 */
public final class TestFinalJDKDelegate implements ITestFinalJDKDelegate {

//    public Object getProxyInstance() {
//        return Proxy.newProxyInstance(TestFinalJDKDelegate.class.getClassLoader(), TestFinalJDKDelegate.class.getInterfaces(),
//                (proxy, method, args) -> {
//                    System.out.println("开启事务");
//
//                    // 执行目标对象方法
//                    Object returnValue = method.invoke(TestFinalJDKDelegate.class, args);
//
//                    System.out.println("提交事务");
//                    return null;
//                });
//    }

    @Override
    public void test() {
        System.out.println("final 子类的输出");
    }

    public static void main(String[] args) {
        TestFinalJDKDelegate target = new TestFinalJDKDelegate();
        Object o = Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                (proxy, method, args1) -> {
                    System.out.println("开启事务 method= " + method.getName());

                    // 执行目标对象方法
                    Object returnValue = method.invoke(target, args1);

                    System.out.println("提交事务");
                    return null;
                });

        ITestFinalJDKDelegate o1 = (ITestFinalJDKDelegate) o;
        o1.test();
    }
}
