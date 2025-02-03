package io.reactivestax;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;

import java.util.function.Supplier;

public interface TestDataProvider {

    //Payload
    Supplier<String> validTradePayloadSupplier = () -> "TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadLengthSupplier = () -> "TDB_00000001,TDB_CUST_2517563,TSLA,SELL,45,1480.82";
    Supplier<String> invalidPayloadAccountNumberSupplier = () -> "TDB_00000001";
    Supplier<String> emptyTradePayloadSupplier = () -> "";
    Supplier<String> nullTradePayloadSupplier = () -> null;

    //RawPayload
    Supplier<RawPayload> validRawPayloadSupplier = () ->
                RawPayload.builder()
                    .tradeID("TDB_00000001")
                    .payload("TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82")
                    .status("Valid")
                    .lookupStatus("Not Posted")
                    .postedStatus("Not Posted")
                .build();

    Supplier<RawPayload> invalidRawPayloadSupplier = () ->
            RawPayload.builder()
                    .tradeID("TDB_00000001")
                    .payload("TDB_00000001,TDB_CUST_2517563,TSLA,SELL,45,1480.82")
                    .status("Invalid")
                    .lookupStatus("Not Posted")
                    .postedStatus("Not Posted")
                    .build();

    Supplier<RawPayload> nullRawPayloadSupplier = () -> RawPayload.builder().build();

    //Trade ID
    Supplier<String> validTradeIdSupplier = () -> "TDB_00000001";

    //Account Number
//    Supplier<String> validAccountNumberSupplier = () -> "TDB_CUST_2517563";

    //TradeIdentifier
    Supplier<TradeIdAndAccNum> validTradeIdentifierSupplier = () -> new TradeIdAndAccNum("TDB_00000001", "TDB_CUST_2517563");

    //File Paths
    Supplier<String> invalidFilePathSupplier = () -> "non-existing-file.csv";
    Supplier<String> trades_0_FilePathSupplier = () -> "0trades.csv";
    Supplier<String> trades_1_FilePathSupplier = () -> "1trades.csv";
    Supplier<String> trades_10000_FilePathSupplier = () -> "10000trades.csv";
    Supplier<String> trades_1000_FilePathSupplier = () -> "1000trades.csv";
    Supplier<String> trades_800_FilePathSupplier = () -> "800trades.csv";
    Supplier<String> trades_999_FilePathSupplier = () -> "999trades.csv";
    Supplier<String> trades_9999_FilePathSupplier = () -> "9999trades.csv";
    Supplier<String> trades_1111_FilePathSupplier = () -> "1111trades.csv";
    Supplier<String> trades_10001_FilePathSupplier = () -> "10001trades.csv";

//    Supplier<String> invalidFileAbsPathSupplier = () -> "src/test/resources/trade-files/non-existing-file.csv";
    Supplier<String> trades_0_FileAbsPathSupplier = () -> "src/test/resources/0trades.csv";
//    Supplier<String> trades_1_FileAbsPathSupplier = () -> "src/test/resources/1trades.csv";
    Supplier<String> trades_10000_FileAbsPathSupplier = () -> "src/test/resources/10000trades.csv";
//    Supplier<String> trades_1000_FileAbsPathSupplier = () -> "src/test/resources/1000trades.csv";
    Supplier<String> trades_800_FileAbsPathSupplier = () -> "src/test/resources/800trades.csv";
    Supplier<String> trades_999_FileAbsPathSupplier = () -> "src/test/resources/999trades.csv";
//    Supplier<String> trades_9999_FileAbsPathSupplier = () -> "src/test/resources/9999trades.csv";
    Supplier<String> trades_1111_FileAbsPathSupplier = () -> "src/test/resources/1111trades.csv";
//    Supplier<String> trades_10001_FileAbsPathSupplier = () -> "src/test/resources/10001trades.csv";
}
