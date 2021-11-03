package com.cs.coding.test;

import com.cs.coding.test.service.impl.LogFileParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsApplication implements CommandLineRunner {

	@Autowired
	LogFileParser logFileParser;

	public static void main(String[] args) {
		SpringApplication.run(CsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(args.length > 0){
			logFileParser.parseFile(args[0]);
		}
	}
}
