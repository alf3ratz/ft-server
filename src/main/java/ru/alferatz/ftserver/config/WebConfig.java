package ru.alferatz.ftserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/api/**")
//        .allowedOrigins("http://localhost:8080")
//        .allowedOrigins("https://ftapp-aapetropavlovskiy.b4a.run")
//        .allowedMethods("*")
//        .allowedHeaders("*")
//        .allowCredentials(false)
//        .maxAge(3600);
//  }
}