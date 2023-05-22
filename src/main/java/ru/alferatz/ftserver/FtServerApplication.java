package ru.alferatz.ftserver;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class FtServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FtServerApplication.class, args);
  }

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("https://localhost:3000","https://6466e6d30012ed2bf9aa3748--serene-semolina-0dd3f7.netlify.app"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays
        .asList("Authorization", "Requestor-Type", "Access-Control-Allow-Origin", "Content-Type","Strict-Origin-When-Cross-Origin"));
    configuration.setExposedHeaders(Arrays
        .asList("X-Get-Header", "Access-Control-Allow-Origin", "X-Content-Type-Options",
            "X-Frame-Options"));
//    configuration.setAllowedOrigins(Collections.singletonList("*"));
//    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
//    configuration.setAllowedMethods(Collections.singletonList("*"));
//    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}
