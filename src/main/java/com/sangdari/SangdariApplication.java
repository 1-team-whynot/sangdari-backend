package com.sangdari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SangdariApplication {

    public static void main(String[] args) {
        SpringApplication.run(SangdariApplication.class, args);
    }

}
