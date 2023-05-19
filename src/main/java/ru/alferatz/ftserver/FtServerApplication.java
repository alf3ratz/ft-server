package ru.alferatz.ftserver;

import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class FtServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FtServerApplication.class, args);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
        "https://6466e6d30012ed2bf9aa3748--serene-semolina-0dd3f7.netlify.app"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Arrays
        .asList("Authorization", "Requestor-Type", "Access-Control-Allow-Origin", "Content-Type"));
    configuration.setExposedHeaders(Arrays
        .asList("X-Get-Header", "Access-Control-Allow-Origin", "X-Content-Type-Options",
            "X-Frame-Options"));
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
