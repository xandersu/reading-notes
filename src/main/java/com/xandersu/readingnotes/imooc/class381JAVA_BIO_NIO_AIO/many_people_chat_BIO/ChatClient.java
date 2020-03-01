package com.xandersu.readingnotes.imooc.class381JAVA_BIO_NIO_AIO.many_people_chat_BIO;

import java.io.*;
import java.net.Socket;

/**
 * @author: suxun
 * @date: 2020/3/1 13:20
 * @description:
 */
public class ChatClient {

    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final String DEFAULT_SERVER_PORT = "127.0.0.1";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public void send(String msg) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    public void start() {
        try {
            socket = new Socket(DEFAULT_SERVER_PORT, DEFAULT_PORT);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new Thread(new UserInputHandler(this)).start();

            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
}
