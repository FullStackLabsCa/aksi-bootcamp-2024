package io.reactivestax.service.interfaces;

public interface TradesFileReading {

    void readFileAndCreateChunks(String filePath, String fileType);

}
