package com.xandersu.readingnotes.imooc.class205_hbase_springboot_distributed_storage;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author suxun
 * @description:
 * @data: 2020/6/15 09:47
 */
public class HBaseFilterTest {

    public void createTable() {
        HBaseUtil.createTable("fileTable", new String[]{"fileInfo", "saveInfo"});
    }

    public void addFileDetails() {
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "name", "fil.txt");
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "type", "txt");
        HBaseUtil.putRow("fileTable", "rowKey1", "fileInfo", "size", "1024");
    }

    public void rowFilterTest() {
        RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("rowkey1")));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Collections.singletonList(filter));
        ResultScanner scanner = HBaseUtil.getScanner("fileTable", "rowKey1", "rowKey2", filterList);
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey= " + Bytes.toString(result.getRow()));
                System.out.println("fileName= " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
            scanner.close();
        }
    }

    public void prefixFilterTest() {
        PrefixFilter filter = new PrefixFilter(Bytes.toBytes("rowkey1"));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Collections.singletonList(filter));
        ResultScanner scanner = HBaseUtil.getScanner("fileTable", "rowKey1", "rowKey2", filterList);
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey= " + Bytes.toString(result.getRow()));
                System.out.println("fileName= " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
            scanner.close();
        }
    }

    public void keyOnlyFilterTest() {
        KeyOnlyFilter filter = new KeyOnlyFilter(true);
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Collections.singletonList(filter));
        ResultScanner scanner = HBaseUtil.getScanner("fileTable", "rowKey1", "rowKey2", filterList);
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey= " + Bytes.toString(result.getRow()));
                System.out.println("fileName= " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
            scanner.close();
        }
    }

    public void columnPrefixFilterTest() {
        ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes("name"));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Collections.singletonList(filter));
        ResultScanner scanner = HBaseUtil.getScanner("fileTable", "rowKey1", "rowKey2", filterList);
        if (scanner != null) {
            scanner.forEach(result -> {
                System.out.println("rowkey= " + Bytes.toString(result.getRow()));
                System.out.println("fileName= " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
            });
            scanner.close();
        }
    }
}
