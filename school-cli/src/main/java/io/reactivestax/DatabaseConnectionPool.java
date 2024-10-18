import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class DatabaseConnectionPool {

    private static HikariDataSource dataSource;

    static {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/bootcamp");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("password123");
        hikariConfig.setMaximumPoolSize(10); // Set max connections in pool
        hikariConfig.setConnectionTimeout(30000); // Timeout in milliseconds
        hikariConfig.setIdleTimeout(600000); // Idle timeout before connection is closed

        // Create the HikariCP data source
        dataSource = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }

    public static void close() {
        // Close the data source (usually when the app shuts down)
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
