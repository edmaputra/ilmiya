package com.ilmiya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IlmiyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(IlmiyaApplication.class, args);
    }
}
