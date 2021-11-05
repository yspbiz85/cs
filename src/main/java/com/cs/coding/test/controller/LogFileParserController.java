package com.cs.coding.test.controller;

import com.cs.coding.test.service.impl.LogFileParserService;
import com.cs.coding.test.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
            System.out.println(System.lineSeparator()+"**** LOG FILE PARSER ***"+System.lineSeparator());
            while (true) {
                System.out.println("To parse the log file, press 1");
                System.out.println("To find all log event, press 2");
                System.out.println("To find log event by id, press 3");
                System.out.println("To find log event by alert type, press 4");
                System.out.println("To generate dummy log file, press 5");
                System.out.println("To exit, press 6");
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
                        System.out.println("Enter path to file (Sample - D:\\temp\\logfile.txt) :: ");
                        String logFilePath = reader.readLine();
                        System.out.println("Enter number of log event (Sample - 10) :: ");
                        int maxData = Integer.valueOf(reader.readLine());
                        FileUtil.generateLogFile(logFilePath,maxData);
                        break;

                    case 6:
                        System.out.println("Exiting the application");
                        System.exit(0);

                    default:
                        System.out.println("Incorrect input!!! Please re-enter choice from our menu");
                }
            }
        } catch (Exception exception) {
            logger.error("Internal Error : {}", exception.getMessage());
        }
    }
}
