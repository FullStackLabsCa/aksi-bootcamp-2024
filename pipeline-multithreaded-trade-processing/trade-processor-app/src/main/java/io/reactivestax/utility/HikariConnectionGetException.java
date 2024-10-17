package io.reactivestax.utility;

import java.sql.SQLException;

public class HikariConnectionGetException extends RuntimeException {
    public HikariConnectionGetException(SQLException e) {
    }
}
