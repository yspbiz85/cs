package com.cs.coding.test.controller;

import com.cs.coding.test.service.impl.LogFileParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.Scanner;

@Controller
public class LogFileParserController {

    private LogFileParserService logFileParserService;

    @Autowired
    LogFileParserController(LogFileParserService logFileParserService) {
        this.logFileParserService = logFileParserService;
    }

    public void parseLogFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("**** LOG FILE PARSER ***");
        while(true){
            System.out.println("To parse the log file, press 1");
            System.out.println("To find all log event, press 2");
            System.out.println("To find log event by id, press 3");
            System.out.println("To find log event by alert type, press 4");
            System.out.println("To exit, press 5");
            System.out.println("Enter your choice :: ");
            int choice = scanner.nextInt();
            switch(choice){
                case 1:
                    System.out.println("Enter the path to log file:: ");
                    String path = scanner.next();
                    logFileParserService.parseFile(path);
                    break;

                case 2:
                    logFileParserService.finaAllEventData();
                    break;

                case 3:
                    System.out.println("Enter the log event id to search:: ");
                    String eventId = scanner.next();
                    logFileParserService.finaEventById(eventId);
                    break;

                case 4:
                    System.out.println("Enter the alert flag to search (true or false):: ");
                    Boolean alertFlag = scanner.nextBoolean();
                    logFileParserService.finaEventByAlert(alertFlag);
                    break;

                case 5:
                    System.out.println("Exiting the application");
                    System.exit(0);

                default:
                    System.out.println("Incorrect input!!! Please re-enter choice from our menu");
            }
        }
    }
}
