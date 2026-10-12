package com.college.traffic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Lets the React dev server (another port) call this API. Origins come from configuration. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.cors-allowed-origins}") private String[] origins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(origins).allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
