//package com.tien.storageservice_3.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class KeyCloakSecurityConfig {
//    //        private final CustomerAccessFilter customerAccessFilter;
////        private final JwtAuthConverter jwtAuthConverter;
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/api/auth/register",
//                                "/api/auth/login",
//                                "/api/auth/refresh-token",
//                                "/api/auth/admin-token").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/users", "/api/roles", "/api/permissions").permitAll()
//                        .anyRequest().authenticated())
////                .addFilterAfter(customerAccessFilter, BearerTokenAuthenticationFilter.class)
////                .oauth2ResourceServer(oauth2 -> oauth2 //this application has role is Authorization server to verify jwt with a public key
////                        .jwt(jwt -> jwt
////                                .jwtAuthenticationConverter(jwtAuthConverter)
////                        ))
//                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        return http.build();
//    }
//}
