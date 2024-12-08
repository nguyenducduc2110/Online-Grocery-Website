package com.springboot3.Web.of.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class WebOfSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebOfSpringBootApplication.class, args);
        System.out.println("Resident");

    }
}
