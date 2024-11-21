package io.reactivestax;

import io.reactivestax.service.TradeProcessor;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {
        TradeProcessor tradeProcessor = new TradeProcessor();
        (new Thread(tradeProcessor::startTradesProcessing)).start();
    }
}
