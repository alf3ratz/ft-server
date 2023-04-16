package ru.alferatz.ftserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
@PropertySources({@PropertySource(value = {"file:${application.properties}"})})
public class FtServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FtServerApplication.class, args);
  }

}
