package io.reactivestax.model;

import java.util.Date;

public class Trade {
    private String tradeID;
    private Date transactionTime;
    private String accountNumber;
    private String cusip;
    private String activity;
    private int quantity;
    private double price;

    public Trade(String tradeID, Date transactionTime, String accountNumber, String cusip, String activity, int quantity, double price) {
        this.tradeID = tradeID;
        this.transactionTime = transactionTime;
        this.accountNumber = accountNumber;
        this.cusip = cusip;
        this.activity = activity;
        this.quantity = quantity;
        this.price = price;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCusip() {
        return cusip;
    }

    public void setCusip(String cusip) {
        this.cusip = cusip;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
