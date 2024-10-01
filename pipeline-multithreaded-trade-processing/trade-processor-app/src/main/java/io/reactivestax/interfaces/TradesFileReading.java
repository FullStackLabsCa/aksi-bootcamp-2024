package io.reactivestax.interfaces;

public interface TradesFileReading {

    chunksPathAndNumberOfChunks readFileAndCreateChunks(String filePath, String fileType);

}
