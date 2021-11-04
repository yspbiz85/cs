package com.cs.coding.test.controller;

import com.cs.coding.test.model.LogEventModel;
import com.cs.coding.test.service.impl.LogFileParserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Controller
public class LogFileParserController {

    private static final Logger logger = LoggerFactory.getLogger(LogFileParserController.class);

    private LogFileParserService logFileParserService;

    @Autowired
    LogFileParserController(LogFileParserService logFileParserService) {
        this.logFileParserService = logFileParserService;
    }

    public void parseLogFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("**** LOG FILE PARSER ***");
            while (true) {
                System.out.println("To parse the log file, press 1");
                System.out.println("To find all log event, press 2");
                System.out.println("To find log event by id, press 3");
                System.out.println("To find log event by alert type, press 4");
                System.out.println("To exit, press 5");
                System.out.println("Enter your choice :: ");
                int choice = Integer.valueOf(reader.readLine());

                switch (choice) {
                    case 1:
                        System.out.println("Enter the absolute path to log file:: ");
                        String path = reader.readLine();
                        logFileParserService.parseFile(path);
                        break;

                    case 2:
                        logFileParserService.finaAllEventData();
                        break;

                    case 3:
                        System.out.println("Enter the log event id to search:: ");
                        String eventId = reader.readLine();
                        logFileParserService.finaEventById(eventId);
                        break;

                    case 4:
                        System.out.println("Enter the alert flag to search (true or false):: ");
                        Boolean alertFlag = Boolean.valueOf(reader.readLine());
                        logFileParserService.finaEventByAlert(alertFlag);
                        break;

                    case 5:
                        System.out.println("Exiting the application");
                        System.exit(0);

                    default:
                        System.out.println("Incorrect input!!! Please re-enter choice from our menu");
                }
            }
        } catch (Exception exception) {
            logger.error("Internal Error : {}",exception.getMessage());
        }
    }

    public void generateLogFile() {
        ObjectMapper mapper = new ObjectMapper();

        for(int i=1;i<100000;i++){
            LogEventModel logEventModel = new LogEventModel();
            logEventModel.setId("scsmbstgra"+i);
            logEventModel.setState("STARTED");
            logEventModel.setHost("123456");
            logEventModel.setType("APPLICATION_LOG");
            logEventModel.setTimestamp(System.currentTimeMillis()+(int)(Math.random()*10));
            String line = null;
            try {
                line = mapper.writeValueAsString(logEventModel)+System.lineSeparator();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get("logfile.txt"),line.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i=1;i<100000;i++){
            LogEventModel logEventModel = new LogEventModel();
            logEventModel.setId("scsmbstgra"+i);
            logEventModel.setState("FINISHED");
            logEventModel.setHost("123456");
            logEventModel.setType("APPLICATION_LOG");
            logEventModel.setTimestamp(System.currentTimeMillis()+(int)(Math.random()*10));
            String line = null;
            try {
                line = mapper.writeValueAsString(logEventModel)+System.lineSeparator();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get("logfile.txt"),line.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
