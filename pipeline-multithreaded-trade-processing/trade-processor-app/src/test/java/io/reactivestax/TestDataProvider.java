package io.reactivestax;

import io.reactivestax.model.Trade;

import java.util.Date;
import java.util.function.Supplier;

public class TestDataProvider {

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
}
