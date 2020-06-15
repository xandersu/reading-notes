package com.xandersu.readingnotes.imooc.class205_hbase_springboot_distributed_storage;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author suxun
 * @description:
 * @data: 2020/6/14 21:02
 */
public class HBaseUtil {

    public static boolean createTable(String tableName, String[] cfs) {
        try {
            HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();
            if (admin.tableExists(TableName.valueOf(tableName))) {
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cf);
                hColumnDescriptor.setMaxVersions(1);
                tableDescriptor.addFamily(hColumnDescriptor);
            });
            admin.createTable(tableDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean deleteTable(String tableName) {
        try {
            HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean putRow(String tableName, String rowKey, String cfName, String qualifier, String data) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier), Bytes.toBytes(data));
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean putRows(String tableName, List<Put> puts) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            table.put(puts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static Result getRow(String tableName, String rowKey) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.get(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result getRow(String tableName, String rowKey, FilterList filterList) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.setFilter(filterList);
            return table.get(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultScanner getScanner(String tableName) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultScanner getScanner(String tableName, String startRow, String endRow) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(endRow));
            scan.setCaching(1000);
            return table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultScanner getScanner(String tableName, String startRow, String endRow, FilterList filterList) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRow));
            scan.setStopRow(Bytes.toBytes(endRow));
            scan.setCaching(1000);
            scan.setFilter(filterList);
            return table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteRow(String tableName, String rowKey) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean deleteColumnFamily(String tableName, String cfName) {
        try {
            HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin();
            admin.deleteColumn(TableName.valueOf(tableName), Bytes.toBytes(cfName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean deleteQualifier(String tableName, String rowKey, String cfName, String qualifier) {
        try {
            Table table = HBaseConn.getHBaseConn().getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifier));
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
