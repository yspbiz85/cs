package com.cs.coding.test;

import com.cs.coding.test.controller.LogFileParserController;
import com.cs.coding.test.service.impl.LogFileParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
@EnableAutoConfiguration
public class CsApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(CsApplication.class);

	@Autowired
	LogFileParserController logFileParserController;

	public static void main(String[] args) {
		SpringApplication.run(CsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		this.logFileParserController.parseLogFile();
	}
}
