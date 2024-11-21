package io.reactivestax.model;

import lombok.*;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return quantity == trade.quantity && Double.compare(price, trade.price) == 0 && Objects.equals(tradeID, trade.tradeID) && Objects.equals(accountNumber, trade.accountNumber) && Objects.equals(cusip, trade.cusip) && Objects.equals(activity, trade.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeID, accountNumber, cusip, activity, quantity, price);
    }
}
