package com.olympus.uga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UgaApplication {

	public static void main(String[] args) {
		SpringApplication.run(UgaApplication.class, args);
	}

}
