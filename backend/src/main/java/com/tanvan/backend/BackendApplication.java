package com.tanvan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BackendApplication {

    public static void main(String[] args) {
        log.info("Server running at: http://localhost:8080");
        log.info("API Docs: http://localhost:8080/swagger-ui/index.html");
        SpringApplication.run(BackendApplication.class, args);
    }

}
