package com.tien.storageservice_3.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("storage service api")
                .description("management storage service with Cloudinary API"))
                .servers(List.of(new Server().url("http://localhost:8080").description("storage service")));
    }

    @Bean
    public GroupedOpenApi groupOpenApi() {
        return GroupedOpenApi.builder()
                .group("api_service") //ten ben duoi title, //cai nay la chi dinh tung service mot trong thang microservice
                .packagesToScan("com.tien.storageservice_3.controller") //scan tat cả cac bean controller nam trong package de lay ve duoc toan bo cac enpoit
                .build();
    }
}
