package com.xandersu.readingnotes.imooc.class381JAVA_BIO_NIO_AIO.many_people_chat_BIO;

import java.io.IOException;
import java.util.Random;

/**
 * @author: suxun
 * @date: 2020/3/1 13:36
 * @description:
 */
public class UserInputHandler implements Runnable {

    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
//                String input = bufferedReader.readLine();
                String input = "";
                for (int i = 0; i < 100; i++) {
                    int millis = new Random().nextInt(10);
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    input = "第" + i + "次通信，间隔" + millis;
                    chatClient.send(input);
                }
                if (chatClient.readyToQuit(input)) {
                    System.out.println("客户端退出");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
