package com.cs.coding.test;

import com.cs.coding.test.controller.LogFileParserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class CsApplication implements CommandLineRunner {
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
