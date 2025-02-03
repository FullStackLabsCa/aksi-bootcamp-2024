package io.reactivestax.utility.database;

public interface TransactionUtil {
    void startTransaction();
    void commitTransaction();
    void rollbackTransaction();
}
