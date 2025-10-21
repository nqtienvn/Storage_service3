package com.tien.storageservice_3.config;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final KeycloakProperties keycloakProperties;
    @Value("${spring.cloudinary.cloud_name}")
    private String cloudName;
    @Value("${spring.cloudinary.api_key}")
    private String apiKey;
    @Value("${spring.cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }

//    @Bean
//    public Keycloak keycloak() {
//        return KeycloakBuilder.builder()
//                .serverUrl(keycloakProperties.getAuthServerUrl())
//                .realm(keycloakProperties.getRealm())
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId(keycloakProperties.getClientId())
//                .clientSecret(keycloakProperties.getClientSecret())
//                .build();
//    }
}
