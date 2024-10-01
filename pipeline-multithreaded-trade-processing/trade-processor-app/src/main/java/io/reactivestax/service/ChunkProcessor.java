package io.reactivestax.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class ChunkProcessor{

    public static final ConcurrentHashMap<String, Integer> accToQueueMap = new ConcurrentHashMap<>();

    public static final LinkedBlockingDeque<String> tradeIdQueue1 = new LinkedBlockingDeque<>();
    public static final LinkedBlockingDeque<String> tradeIdQueue2 = new LinkedBlockingDeque<>();
    public static final LinkedBlockingDeque<String> tradeIdQueue3 = new LinkedBlockingDeque<>();

    public void startChunkProcessorPool(String folderPath, int numberOfFiles) {
        List<String> filePaths = getFilesFromFolder(folderPath);

        int numberOfThreads = 10;
        int filesSubmitted = 0;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            if(filesSubmitted <= numberOfFiles) {
                executorService.submit(new ChunkProcessorTask(filePaths.get(filesSubmitted)));
                filesSubmitted++;
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
