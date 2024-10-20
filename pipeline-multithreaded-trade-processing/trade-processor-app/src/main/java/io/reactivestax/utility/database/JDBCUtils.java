package io.reactivestax.utility.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.utility.exceptions.HikariConnectionGetException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class JDBCUtils implements ConnectionUtil<Connection>, TransactionUtil {
    private static JDBCUtils instance;
    private static DataSource dataSource;
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();


    public JDBCUtils() {
        // Private Constructor to Avoid Instance creation for this Class
    }

    public static synchronized JDBCUtils getInstance(){
        if(instance == null) instance = new JDBCUtils();
        return instance;
    }

    @Override
    public Connection getConnection() {
        Connection connection  = connectionHolder.get();
        if(connection == null) {
            dataSource = getHikariDataSource();
            try {
                connection = dataSource.getConnection();
                connectionHolder.set(connection);
            } catch (SQLException e) {
                System.out.println("Error Getting Connection from Datasource....");
                throw new HikariConnectionGetException(e);
            }
        }
        return connection;
    }

    private static DataSource getHikariDataSource(){
        if(dataSource == null) configureHikariCP(getFileProperty("db.port.num"), getFileProperty("db.name"));
        return dataSource;
    }

    private static void configureHikariCP(String portNum, String dbName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName);
        config.setUsername(getFileProperty("db.username"));
        config.setPassword(getFileProperty("db.password"));

        // Optional HikariCP settings
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(5); // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout for obtaining a connection
        config.setIdleTimeout(600000); // 10 minutes idle timeout

        dataSource = new HikariDataSource(config);
    }

    @Override
    public void startTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Failed to start Transaction....");
        }
    }

    @Override
    public void commitTransaction() {
        try {
            getConnection().commit();
            getConnection().setAutoCommit(true);
            closeConnection();
        } catch (SQLException e) {
            System.out.println("Failed to commit Transaction....");
        }

    }

    @Override
    public void rollbackTransaction() {
        try {
            getConnection().rollback();
            getConnection().setAutoCommit(true);
            closeConnection();
        } catch (SQLException e) {
            System.out.println("Failed to rollback Transaction....");
        }
    }

    private void closeConnection(){
        try {
            getConnection().close();
        } catch (SQLException e) {
            System.out.println("Failed to Close Transaction....");
        } finally {
            connectionHolder.remove();
        }
    }
}
