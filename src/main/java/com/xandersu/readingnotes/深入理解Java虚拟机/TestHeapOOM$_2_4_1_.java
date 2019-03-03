package com.xandersu.readingnotes.深入理解Java虚拟机;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author: suxun
 * @Date: 2019/3/3 16:05
 * @Description: 2.4.1 Java 堆溢出
 */
public class TestHeapOOM$_2_4_1_ {

    static class OOMObj {

    }

    public static void main(String[] args) {
        List<OOMObj> list = Lists.newArrayList();
        while (true)
            list.add(new OOMObj());
    }

}
