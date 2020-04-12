package com.xandersu.readingnotes.imooc.class381_JAVA_BIO_NIO_AIO.many_people_chat_BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author: suxun
 * @date: 2020/3/1 11:12
 * @description:
 */
public class ChatHandler implements Runnable {
    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = null;
            while ((msg = bufferedReader.readLine()) != null) {
                String fwdMsg = "客户端" + socket.getPort() + "发送：" + msg;
                System.out.println(fwdMsg);
                chatServer.forwardMessage(socket, fwdMsg + "\n");

                if (chatServer.readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
