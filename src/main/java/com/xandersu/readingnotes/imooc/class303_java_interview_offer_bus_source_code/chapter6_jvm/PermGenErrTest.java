package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code.chapter6_jvm;

import java.util.Random;

/**
 * @author: suxun
 * @date: 2020/4/25 13:48
 * @description:
 */
public class PermGenErrTest {

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            String intern = getRandomString(100000).intern();
        }
        System.out.println("Mission complete");
    }

    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        int strLength = str.length();

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(strLength);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
