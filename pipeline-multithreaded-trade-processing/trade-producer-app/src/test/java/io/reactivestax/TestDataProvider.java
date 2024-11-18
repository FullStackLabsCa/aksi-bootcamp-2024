package io.reactivestax;

import java.util.function.Supplier;

public interface TestDataProvider {

    Supplier<String> validTradePayloadSupplier = () -> "TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadLengthSupplier = () -> "TDB_00000001,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadAccountNumberSupplier = () -> "TDB_00000001";
    Supplier<String> emptyTradePayloadSupplier = () -> "";
    Supplier<String> nullTradePayloadSupplier = () -> null;
    Supplier<String> validTradeIdSupplier = () -> "TDB_00000001";
}
