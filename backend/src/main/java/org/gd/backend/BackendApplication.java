package org.gd.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.TimeZone;

@ConfigurationPropertiesScan
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        // Windows reports the legacy "Asia/Calcutta" ID, which Postgres rejects
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(BackendApplication.class, args);
    }

}
