package com.xandersu.readingnotes.imooc.class381_JAVA_BIO_NIO_AIO.socket_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: suxun
 * @date: 2019/12/3 22:35
 * @description:
 */
public class Server {

    public static void main(String[] args) {
        int port = 8888;
        ServerSocket serverSocket = null;
        //绑定监听端口
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("启动server，监听端口=" + port);
            while (true) {
                //等待客户端连接，阻塞的
                Socket socket = serverSocket.accept();
                System.out.println("客户端[" + socket.getPort() + "]已经连接");
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String msg = null;
                while ((msg = br.readLine()) != null) {
                    //读取客户端发送的消息
                    System.out.println("客户端[" + socket.getPort() + "]=" + msg);
                    //回复客户的消息
                    bw.write("服务器回复=" + msg + "\n");
                    bw.flush();
                    //客户端退出
                    if (msg.equals("quit")) {
                        System.out.println("客户端[" + socket.getPort() + "]退出");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
