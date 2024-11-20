package io.reactivestax;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;

import java.util.function.Supplier;

public interface TestDataProvider {

    //Payload
    Supplier<String> validTradePayloadSupplier = () -> "TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadLengthSupplier = () -> "TDB_00000001,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadAccountNumberSupplier = () -> "TDB_00000001";
    Supplier<String> emptyTradePayloadSupplier = () -> "";
    Supplier<String> nullTradePayloadSupplier = () -> null;

    //Trade ID
    Supplier<String> validTradeIdSupplier = () -> "TDB_00000001";

    //TradeIdentifier
    Supplier<TradeIdAndAccNum> validTradeIdentifierSupplier = () -> new TradeIdAndAccNum("TDB_00000001", "TDB_CUST_2517563");

    //File Paths
    Supplier<String> invalidFilePathSupplier = () -> "src/test/resources/trade-files/non-existing-file.csv";
    Supplier<String> trades_0_FilePathSupplier = () -> "src/test/resources/0trades.csv";
    Supplier<String> trades_10000_FilePathSupplier = () -> "src/test/resources/10000trades.csv";
    Supplier<String> trades_800_FilePathSupplier = () -> "src/test/resources/800trades.csv";
    Supplier<String> trades_999_FilePathSupplier = () -> "src/test/resources/999trades.csv";
    Supplier<String> trades_1111_FilePathSupplier = () -> "src/test/resources/1111trades.csv";
}
