package com.company.video_service.dto;

public class TestSplitVideoRequest {

    private String inputFilePath;
    private String outputFolderPath;
    private Integer chunkSizeBytes;

    public TestSplitVideoRequest() {
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFolderPath() {
        return outputFolderPath;
    }

    public void setOutputFolderPath(String outputFolderPath) {
        this.outputFolderPath = outputFolderPath;
    }

    public Integer getChunkSizeBytes() {
        return chunkSizeBytes;
    }

    public void setChunkSizeBytes(Integer chunkSizeBytes) {
        this.chunkSizeBytes = chunkSizeBytes;
    }
}
