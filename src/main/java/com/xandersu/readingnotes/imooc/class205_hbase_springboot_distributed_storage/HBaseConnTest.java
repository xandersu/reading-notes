package com.xandersu.readingnotes.imooc.class205_hbase_springboot_distributed_storage;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * @author suxun
 * @description:
 * @data: 2020/6/14 20:55
 */
public class HBaseConnTest {

    public void getConnTest(){
        Connection conn = HBaseConn.getHBaseConn();
        System.out.println(conn.isClosed());
        HBaseConn.closeConn();
        System.out.println(conn.isClosed());
    }

    public void getTableTest(){

        try {
            Table table = HBaseConn.getTable("uUS_POPULATION");
            System.out.println(table.getName().getNameAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
