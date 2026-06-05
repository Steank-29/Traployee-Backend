package com.traployee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TraployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TraployeeApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 Traployee Backend Started Successfully!");
        System.out.println("📡 API available at: http://localhost:8080");
        System.out.println("========================================");
    }
}