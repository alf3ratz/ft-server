package ru.alferatz.ftserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@EnableAuthorizationServer
public class FtServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtServerApplication.class, args);
    }

}
