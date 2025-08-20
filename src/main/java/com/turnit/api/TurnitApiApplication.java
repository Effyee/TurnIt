package com.turnit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.turnit.api")
@EnableJpaRepositories(basePackages = "com.turnit.api.repository")
@EntityScan(basePackages = "com.turnit.api.domain")
public class TurnitApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(TurnitApiApplication.class, args);
	}
}