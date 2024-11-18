package io.reactivestax;

import io.reactivestax.model.Trade;

import java.util.Date;
import java.util.function.Supplier;

public interface TestDataProvider {

    Supplier<Trade> goodTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("BUY")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("TSLA")
            .quantity(10)
            .build();

    Supplier<Trade> badTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("BUY")
            .price(0.0)
            .transactionTime(new Date())
            .cusip("TSLA")
            .quantity(10)
            .build();

    Supplier<Trade> goodBuyTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("BUY")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("TSLA")
            .quantity(10)
            .build();

    Supplier<Trade> invalidCusipTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("BUY")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("Invalid")
            .quantity(10)
            .build();

    Supplier<Trade> goodSellTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("SELL")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("TSLA")
            .quantity(10)
            .build();

    Supplier<Trade> invalidActivityTradeSupplier = () -> Trade.builder()
            .tradeID("TD123")
            .accountNumber("123")
            .activity("Invalid")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("TSLA")
            .quantity(10)
            .build();

    Supplier<String> validTradePayloadSupplier = () -> "TDB_00000001,2024-09-25 06:58:37,TDB_CUST_2517563,TSLA,SELL,45,1480.82";

    Supplier<Trade> validTradeForPayloadSupplier = () -> Trade.builder()
            .tradeID("TDB_00000001")
            .accountNumber("123")
            .activity("BUY")
            .price(0.0)
            .transactionTime(new java.sql.Date(2024))
            .cusip("TSLA")
            .quantity(10)
            .build();
}
