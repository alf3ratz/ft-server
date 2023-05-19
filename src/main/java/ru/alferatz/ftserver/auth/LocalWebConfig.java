//package ru.alferatz.ftserver.auth;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//public class LocalWebConfig extends WebMvcConfigurerAdapter {
//
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//        .allowedOrigins("http://localhost:3000", "https://ftapp-aapetropavlovskiy.b4a.run")
//        .exposedHeaders(
//            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
//        .allowedMethods("*");
//  }
//}
