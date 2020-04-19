package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: suxun
 * @date: 2020/4/19 13:00
 * @description:
 */
public class TcpServer {

    public static final int port = 8086;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("start serverSocket");
        while (true) {
            System.out.println("start accept");
            Socket socket = serverSocket.accept();
            //获取socket的输出流
            OutputStream os = socket.getOutputStream();
            //获取socket的输入流
            InputStream is = socket.getInputStream();
            int ch = 0;
            byte[] buff = new byte[1024];
            //buff主要用来读取输入的内容，存成byte数组，ch主要用来获取读取数组的长度
            ch = is.read(buff);
            //将接收流的byte数组转换成字符串，这里获取的内容是客户端发送过来的字符串参数
            String content = new String(buff, 0, ch);

//            String content = IoUtil.read(is, StandardCharsets.UTF_8);
//            String content = IOUtils.toString(is, StandardCharsets.UTF_8);

            System.out.println(content);
            //往输出流里写入获得的字符串的长度，回发给客户端
            os.write(String.valueOf(content.length()).getBytes());
            //不要忘记关闭输入输出流以及socket
            is.close();
            os.close();
            socket.close();
            System.out.println("serverSocket done");
        }
    }

    private static int rpcDoSth(String clientInput) {
        return clientInput.length();
    }
}
