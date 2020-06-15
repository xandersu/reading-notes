package com.xandersu.readingnotes.imooc.class205_hbase_springboot_distributed_storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * @author suxun
 * @description:
 * @data: 2020/6/14 20:47
 */
@Slf4j
public class HBaseConn {
    private static final HBaseConn INSTANCE = new HBaseConn();
    private static Configuration configuration;
    private static Connection connection;

    private HBaseConn() {
        try {
            if (configuration == null) {
                configuration = HBaseConfiguration.create();
                configuration.set("hbase.zookeeper.quorum", "localhost:2181");
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            try (Connection connection = ConnectionFactory.createConnection(configuration)) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static Connection getHBaseConn() {
        return INSTANCE.getConnection();
    }

    public static Table getTable(String tableName) throws IOException {
        return INSTANCE.getConnection().getTable(TableName.valueOf(tableName));
    }

    public static void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
