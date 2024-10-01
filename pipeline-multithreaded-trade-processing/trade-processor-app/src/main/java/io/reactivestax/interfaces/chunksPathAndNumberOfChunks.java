package io.reactivestax.interfaces;

public record chunksPathAndNumberOfChunks(String folderPath, int numberOfFiles) {

    @Override
    public String toString() {
        return "chunksPathAndNumberOfChunks{" +
                "filePath=" + folderPath +
                ", numberOfFiles=" + numberOfFiles +
                '}';
    }

    @Override
    public String folderPath() {
        return folderPath;
    }

    @Override
    public int numberOfFiles() {
        return numberOfFiles;
    }
}

