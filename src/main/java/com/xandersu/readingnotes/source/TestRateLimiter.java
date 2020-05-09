package com.xandersu.readingnotes.source;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author su
 * @date 2020/5/821:02
 * @description
 */
public class TestRateLimiter {

    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(10);
        for (int i = 0; i < 11; i++) {
            new Thread(() -> {
                rateLimiter.acquire();
                System.out.println("pass");
            }).start();
        }
    }

}
