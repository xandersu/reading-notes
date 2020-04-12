package com.xandersu.readingnotes.imooc.class381_JAVA_BIO_NIO_AIO.many_people_chat_BIO;

import com.google.common.collect.Maps;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * @author: suxun
 * @date: 2020/3/1 10:51
 * @description:
 */
public class ChatServer {

    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private ServerSocket serverSocket;
    private Map<Integer, Writer> connectedClients;

    public ChatServer() {
        connectedClients = Maps.newHashMap();
    }

    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, writer);
            System.out.println("客户端【" + port + "】已连接到服务器");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectedClients.containsKey(port)) {
                connectedClients.get(port).close();
            }
            connectedClients.remove(port);
            System.out.println("客户端【" + port + "】已断开连接");
        }
    }

    public synchronized void forwardMessage(Socket socket, String fwdMsg) throws IOException {
        for (Map.Entry<Integer, Writer> entry : connectedClients.entrySet()) {
            Integer key = entry.getKey();
            Writer value = entry.getValue();
            if (key != socket.getPort()) {
                value.write(fwdMsg);
                value.flush();
            }
        }
    }

    public void start() {
        //绑定监听端口
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器启动，监听端口：" + DEFAULT_PORT + "...");

            while (true) {
                //等待连接
                Socket socket = serverSocket.accept();
                //创建chatHandler
                Thread thread = new Thread(new ChatHandler(this, socket));


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
