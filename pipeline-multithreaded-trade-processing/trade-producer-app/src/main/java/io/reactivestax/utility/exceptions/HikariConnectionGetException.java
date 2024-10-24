package io.reactivestax.utility.exceptions;

import java.sql.SQLException;

public class HikariConnectionGetException extends RuntimeException {
    public HikariConnectionGetException(SQLException e) {
    }
}
