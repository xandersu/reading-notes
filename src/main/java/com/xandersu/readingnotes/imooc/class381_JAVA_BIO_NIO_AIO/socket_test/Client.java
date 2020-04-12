package com.xandersu.readingnotes.imooc.class381_JAVA_BIO_NIO_AIO.socket_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author: suxun
 * @date: 2019/12/3 22:35
 * @description:
 */
public class Client {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8888;
        Socket socket = null;
        try {
            socket = socket = new Socket(host, port);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();

                bw.write(input);
                bw.flush();

                //读取服务器返回的消息
                String msg = br.readLine();
                if (msg != null) {
                    System.out.println("服务器返回=" + msg);
                }
                //用户推出
                if (input.equals("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
