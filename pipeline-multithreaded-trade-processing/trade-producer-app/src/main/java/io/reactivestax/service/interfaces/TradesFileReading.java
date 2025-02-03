package io.reactivestax.interfaces;

public interface TradesFileReading {

    void readFileAndCreateChunks(String filePath, String fileType);

}
