package io.reactivestax;

import io.reactivestax.service.TradeProcessor;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {

        (new Thread(new TradeProcessorRunner())).start();

    }
}

class TradeProcessorRunner implements Runnable{
    @Override
    public void run(){
        TradeProcessor processor = new TradeProcessor();
        processor.startTradeProcessingFromQueues();
    }
}
