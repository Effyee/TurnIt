package com.turnit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableCaching // 캐싱 기능을 활성화하는 어노테이션 추가!
@EnableJpaAuditing
@SpringBootApplication
public class TurnitApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(TurnitApiApplication.class, args);
	}
}