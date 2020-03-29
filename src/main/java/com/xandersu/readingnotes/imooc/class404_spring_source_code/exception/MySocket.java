package com.xandersu.readingnotes.imooc.class404_spring_source_code.exception;

import java.net.ServerSocket;

/**
 * @author: suxun
 * @date: 2020/3/29 12:10
 * @description:
 */
public class MySocket {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(8080);
        serverSocket.accept();
    }
}
