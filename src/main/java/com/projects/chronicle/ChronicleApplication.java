package com.projects.chronicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChronicleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronicleApplication.class, args);
    }

}
