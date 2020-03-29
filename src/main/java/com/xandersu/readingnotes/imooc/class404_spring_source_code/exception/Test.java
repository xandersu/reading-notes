package com.xandersu.readingnotes.imooc.class404_spring_source_code.exception;

/**
 * @author: suxun
 * @date: 2020/3/29 11:27
 * @description:
 */
public class Test {

    public static void main(String[] args) {
        try {
            throw new CException(new BException(new AException(new Exception())));
        } catch (Throwable t) {
            while (t != null) {
                System.out.println(t.getClass());
                t = t.getCause();
            }
        }
    }
}
