package io.reactivestax.interfaces;

public record tradeIdAndAccNum(String tradeID, String accountNumber) {

    @Override
    public String toString() {
        return "tradeIdAndAccNum{" +
                "tradeID='" + tradeID + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
