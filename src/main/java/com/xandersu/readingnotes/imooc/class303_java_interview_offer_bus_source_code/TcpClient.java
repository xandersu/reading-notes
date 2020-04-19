package com.xandersu.readingnotes.imooc.class303_java_interview_offer_bus_source_code;


import cn.hutool.core.io.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author: suxun
 * @date: 2020/4/19 13:00
 * @description:
 */
public class TcpClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", TcpServer.port);
        System.out.println("start socket");

        OutputStream outputStream = socket.getOutputStream();

        InputStream inputStream = socket.getInputStream();

        System.out.println("socket getOutputStream");
        IoUtil.writeUtf8(outputStream, false, "123456");
//        IOUtils.write("123456", outputStream, StandardCharsets.UTF_8);


        System.out.println("socket getInputStream");
        String serverResp = IoUtil.read(inputStream, StandardCharsets.UTF_8);
//        String serverResp = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        System.out.println("serverResp= " + serverResp);
        outputStream.close();
        inputStream.close();
        socket.close();
        System.out.println("socket close");
    }
}
