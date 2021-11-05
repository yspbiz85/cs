package com.cs.coding.test.utils;

import com.cs.coding.test.model.LogEventModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final String START_STATE = "STARTED";
    private static final String FINISH_STATE = "FINISHED";

    public static void generateLogFile(String logFilePath,int maxData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Runnable startRunnable = () -> {
                String line = null;
                for (int i = 1; i < maxData; i++) {
                    try {
                        line = mapper.writeValueAsString(generateLogEventModel(i,START_STATE)) + System.lineSeparator();
                    } catch (JsonProcessingException jsonProcessingException) {
                        logger.error("JsonProcessingException : {}", jsonProcessingException.getMessage());
                    }
                    try {
                        Files.write(Paths.get(logFilePath), line.getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE,StandardOpenOption.APPEND);
                    } catch (IOException ioException) {
                        logger.error("IOException : {}", ioException.getMessage());
                    }
                }
            };
            Runnable finishRunnable = () -> {
                String line = null;
                for (int i = 1; i < maxData; i++) {
                    try {
                        line = mapper.writeValueAsString(generateLogEventModel(i,FINISH_STATE)) + System.lineSeparator();
                    } catch (JsonProcessingException jsonProcessingException) {
                        logger.error("JsonProcessingException : {}", jsonProcessingException.getMessage());
                    }
                    try {
                        Files.write(Paths.get(logFilePath), line.getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.CREATE,StandardOpenOption.APPEND);
                    } catch (IOException ioException) {
                        logger.error("IOException : {}", ioException.getMessage());
                    }
                }
            };
            Thread startThread = new Thread(startRunnable);
            startThread.start();
            Thread finishThread = new Thread(finishRunnable);
            finishThread.start();
        } catch (Exception exception) {
            logger.error("Exception while generating dummy log file : {}", exception.getMessage());
        }
    }

    private static LogEventModel generateLogEventModel(int i,String state){
        return LogEventModel.builder()
                .id("scsmbstgra" + i)
                .state(state)
                .host("123456")
                .type("APPLICATION_LOG")
                .timestamp(System.currentTimeMillis() + (int) (Math.random() * 10))
                .build();
    }
}
