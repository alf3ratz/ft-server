package ru.alferatz.ftserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@EnableFeignClients
//@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.SecurityDataConfiguration .class)
public class FtServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtServerApplication.class, args);
    }

}
