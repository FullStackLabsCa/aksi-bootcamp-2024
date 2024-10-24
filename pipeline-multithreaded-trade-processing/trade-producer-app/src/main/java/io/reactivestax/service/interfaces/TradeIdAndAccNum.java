package io.reactivestax.service.interfaces;

public record TradeIdAndAccNum(String tradeID, String accountNumber) {

    @Override
    public String toString() {
        return "tradeIdAndAccNum{" +
                "tradeID='" + tradeID + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
