package com.example.library_management.launch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.example.library_management")
public class Main extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        try {
            logger.info("Запуск Spring Boot приложения...");
            SpringApplication.run(Main.class, args);
            logger.info("Приложение успешно запущено.");
        } catch (Exception e) {
            logger.error("Ошибка при запуске приложения", e);
        }
    }
}