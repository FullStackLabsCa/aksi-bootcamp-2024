package io.reactivestax.service;

import io.reactivestax.utility.MultithreadTradeProcessorUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChunkProcessor{

    public static final ConcurrentHashMap<String, Integer> accToQueueMap = new ConcurrentHashMap<>();

    public void startChunkProcessorPool(String folderPath) {
        List<String> filePaths = getFilesFromFolder(folderPath);

        int numberOfThreads = Integer.parseInt(MultithreadTradeProcessorUtility.readPropertiesFile().getProperty("threadPoolSizeOfChunkProcessor"));
        int filesSubmitted = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        while (filesSubmitted < filePaths.size()){
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(new ChunkProcessorTask(filePaths.get(filesSubmitted)));
                filesSubmitted++;
                if(filesSubmitted >= filePaths.size()) break;
            }
        }

        executorService.shutdown();
    }

    public List<String> getFilesFromFolder(String folderPath){
        List<String> listOfFiles = new ArrayList<>();

        File directory = new File(folderPath);

        if(directory.exists() && directory.isDirectory()){
            File[] filesList = directory.listFiles();

            if(filesList != null){
                for(File file : filesList){
                    if(file.isFile()){
                        listOfFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }

        return listOfFiles;
    }

}
