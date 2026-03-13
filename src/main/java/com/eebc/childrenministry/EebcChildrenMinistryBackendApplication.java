package com.eebc.childrenministry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EebcChildrenMinistryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EebcChildrenMinistryBackendApplication.class, args);
	}

}
