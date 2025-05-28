package com.boanni_back.project.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvLog implements CommandLineRunner {

    @Value("${RDS_URL}")
    private String url;

    @Value("${RDS_USERNAME}")
    private String user;

    @Value("${RDS_PASSWORD}")
    private String pwd;

    @Override
    public void run(String ...args) {
        System.out.println("✅ RDS_URL: " + url);
        System.out.println("✅ RDS_USERNAME: " + user);
        System.out.println("✅ RDS_PASSWORD: " + pwd);
    }
}
