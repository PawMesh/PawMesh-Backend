package com.pawmesh.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PawMeshBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PawMeshBackendApplication.class, args);
    }
}
