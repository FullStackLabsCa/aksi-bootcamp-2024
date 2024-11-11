package io.reactivestax.model;

import lombok.*;

import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class Trade {
    private String tradeID;
    private Date transactionTime;
    private String accountNumber;
    private String cusip;
    private String activity;
    private int quantity;
    private double price;

    private Trade(){
        // Use Builder for Creating Trade
    }

    public java.sql.Date getTransactionTime() {
        return (java.sql.Date) transactionTime;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeID='" + tradeID + '\'' +
                ", transactionTime=" + transactionTime +
                ", accountNumber='" + accountNumber + '\'' +
                ", cusip='" + cusip + '\'' +
                ", activity='" + activity + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
