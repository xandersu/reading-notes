package com.xandersu.readingnotes.imooc.class205_hbase_springboot_distributed_storage;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author suxun
 * @description:
 * @data: 2020/6/15 09:47
 */
public class HBaseUtilTest {

    public void createTable() {
        HBaseUtil.createTable("fileTable", new String[]{"fileInfo", "saveInfo"});
    }

    public void addFileDetails() {
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "name", "fil.txt");
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "type", "txt");
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "size", "1024");
    }

    public void getFileDetails() {
        Result result = HBaseUtil.getRow("fileTable", "rowKey1");
        if (result != null) {
            System.out.println("rowkey= " + Bytes.toString(result.getRow()));
        }
    }

    public void scanFileDetails() {
        ResultScanner scanner = HBaseUtil.getScanner("fileTable", "rowKey1", "fileInfo");
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey= " + Bytes.toString(result.getRow()));
                System.out.println("fileName= " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
        }
    }

    public void deleteRow() {
        HBaseUtil.deleteRow("fileTable", "rowKey1");
    }
}
