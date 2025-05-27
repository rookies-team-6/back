package com.boanni_back.project.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.boanni_back.project.admin")
@EntityScan(basePackages = "com.boanni_back.project.admin.entity")
@EnableJpaRepositories(basePackages = "com.boanni_back.project.admin.repository")
public class AdminTestApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdminTestApplication.class);
        app.setAdditionalProfiles("admin");
        app.run(args);
    }
}