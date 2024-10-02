package io.reactivestax.service;

import io.reactivestax.interfaces.TradesFileReading;
import io.reactivestax.interfaces.chunksPathAndNumberOfChunks;

import static io.reactivestax.service.ChunkProcessor.chunksPaths;
import static io.reactivestax.utility.MultithreadTradeProcessorUtility.readPropertiesFile;

import java.io.*;
import java.util.Scanner;


public class TradesFileReader implements TradesFileReading {
    @Override
    public void readFileAndCreateChunks(String filePath, String fileType) {
          try(Scanner fileReader = new Scanner(new FileReader(filePath))){

              int numOfFilesGenerated = 0;
              int maxNumOfLines = Integer.parseInt(readPropertiesFile().getProperty("chunkSize"));
              boolean newFileCreationNeeded = true;
              String fileNameUnderProcessing = null;
              int linesRead = 0;
              String folderPath = readPropertiesFile().getProperty("chunkPath");

              if(fileReader.hasNextLine()) {
                  fileReader.nextLine();
              } // Skipping the first line which contains the headers of the columns
              while(fileReader.hasNextLine()) {

                  if(newFileCreationNeeded){
                      fileNameUnderProcessing = folderPath+"/trade_chunk_" + numOfFilesGenerated + ".csv";
                      numOfFilesGenerated++;
                      newFileCreationNeeded = false;
                  }
                  try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameUnderProcessing, true))) {
                      if ((linesRead >= maxNumOfLines) || !fileReader.hasNextLine()) {
                          try {
                              chunksPaths.put(new File(fileNameUnderProcessing).getAbsolutePath());
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                              System.out.println(e.getMessage());
                          }
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

          } catch (IOException e) {
              throw new RuntimeException(e);
          }
    }


}
