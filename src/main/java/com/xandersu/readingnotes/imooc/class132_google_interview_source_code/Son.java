package com.xandersu.readingnotes.imooc.class132_google_interview_source_code;

/**
 * @author: suxun
 * @date: 2020/4/13 22:31
 * @description:
 */
public class Son extends Parent {

//    @Override
//    private void m1() {
//        super.m1();
//    }

    public void m2() {

    }

    @Override
    public int m3(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        Parent parent = new Son();
        Son son = new Son();
        System.out.println(parent.m3(100, 50));
        System.out.println(son.m3(100, 50));


//        Parent parent2 = new Parent();
//        Son son2 = (Son) new Parent();
//        System.out.println(parent2.m3(100, 50));
//        System.out.println(son2.m3(100, 50));
    }
}
