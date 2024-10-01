package io.reactivestax.service;

import multithread_trade_processing.interfaces.TradesFileReading;
import multithread_trade_processing.interfaces.chunksPathAndNumberOfChunks;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class TradesFileReader implements TradesFileReading {
    @Override
    public chunksPathAndNumberOfChunks readFileAndCreateChunks(String filePath, String fileType) {
          try(Scanner fileReader = new Scanner(new FileReader(filePath))){

              int numOfFilesGenerated = 0;
              int maxNumOfLines = getChunkSizeFromPropertiesFile();
              boolean newFileCreationNeeded = true;
              String fileNameUnderProcessing = null;
              int linesRead = 0;
              String folderPath = "boca-bc24-java-core-problems/src/multithread_trade_processing/data/";

              if(fileReader.hasNextLine()) {
                  fileReader.nextLine();
              } // Skipping the first line which contains the headers of the columns
              while(fileReader.hasNextLine()) {

                  if(newFileCreationNeeded){
                    fileNameUnderProcessing = folderPath+"trade_chunk_" + numOfFilesGenerated + ".csv";
                    newFileCreationNeeded = false;
                  }
                  try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameUnderProcessing, true))) {
                      if ((linesRead >= maxNumOfLines) || !fileReader.hasNextLine()) {
                          writer.close();
                          numOfFilesGenerated++;
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

              return new chunksPathAndNumberOfChunks(folderPath, numOfFilesGenerated);
          } catch (IOException e) {
              throw new RuntimeException(e);
          }
    }

    public Integer getChunkSizeFromPropertiesFile(){
            Properties properties = new Properties();

            try (FileInputStream fis = new FileInputStream("boca-bc24-java-core-problems/src/multithread_trade_processing/application.properties")) {
                properties.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Integer.valueOf(properties.getProperty("chunkSize"));
        }
}
