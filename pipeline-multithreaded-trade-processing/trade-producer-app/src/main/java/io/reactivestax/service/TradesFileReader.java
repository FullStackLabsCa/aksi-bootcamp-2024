package io.reactivestax.service;

import io.reactivestax.service.interfaces.TradesFileReading;
import io.reactivestax.utility.exceptions.InvalidFilePathException;
import io.reactivestax.utility.messaging.ChunksStream;

import static io.reactivestax.utility.ApplicationPropertyUtil.getFileProperty;

import java.io.*;
import java.util.Scanner;


public class TradesFileReader implements TradesFileReading {
    @Override
    public void readFileAndCreateChunks(String filePath, String fileType) {
        try (Scanner fileReader = new Scanner(new FileReader(filePath))) {

            int numOfFilesGenerated = 0;
            int maxNumOfLines = Integer.parseInt(getFileProperty("chunkSize"));
            boolean newFileCreationNeeded = true;
            String fileNameUnderProcessing = null;
            int linesRead = 0;
            String folderPath = getFileProperty("chunkPath");

            if (fileReader.hasNextLine()) {
                fileReader.nextLine();
            } // Skipping the first line which contains the headers of the columns
            while (fileReader.hasNextLine()) {

                if (newFileCreationNeeded) {
                    fileNameUnderProcessing = folderPath + "/trade_chunk_" + numOfFilesGenerated + ".csv";
                    numOfFilesGenerated++;
                    newFileCreationNeeded = false;
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameUnderProcessing, true))) {
                    if ((linesRead >= maxNumOfLines) || !fileReader.hasNextLine()) {
                        ChunksStream.produceChunkPath(new File(fileNameUnderProcessing).getAbsolutePath());
                        newFileCreationNeeded = true;
                        linesRead = 0;
                    } else {
                        String data = fileReader.nextLine();
                        linesRead++;
                        writer.write(data);
                        writer.newLine();
                    }
                }
            }

            assert fileNameUnderProcessing != null;
            ChunksStream.produceChunkPath(new File(fileNameUnderProcessing).getAbsolutePath());

        } catch (IOException e) {
            throw new InvalidFilePathException("Unable to find Trades File");
        }
    }


}