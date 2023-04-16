package ru.alferatz.ftserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.*;
//import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@Log4j2
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  //  @Value("${spring.rabbitmq.host}")
//  private final String host;
//
//  @Value("${spring.rabbitmq.username}")
//  private final String username;
//
//  @Value("${spring.rabbitmq.password}")
//  private final String password;
//
//  public static final String TOPIC_DESTINATION_PREFIX = "/topic/";
//  public static final String REGISTRY = "/ws";
//
//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry.addEndpoint(REGISTRY)
//        .withSockJS();
//  }
//
//  @Override
//  public void configureMessageBroker(MessageBrokerRegistry config) {
//    config.enableStompBrokerRelay(TOPIC_DESTINATION_PREFIX)
//        .setRelayHost(host)
//        .setClientLogin(username)
//        .setClientPasscode(password)
//        .setSystemLogin(username)
//        .setSystemPasscode(password);
//  }

  @Value("${spring.rabbitmq.host}")
  String host;

  @Value("${spring.rabbitmq.username}")
  String username;

  @Value("${spring.rabbitmq.password}")
  String password;

  public static final String TOPIC_DESTINATION_PREFIX = "/topic/";
  public static final String REGISTRY = "/ws";

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(REGISTRY)
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableStompBrokerRelay(TOPIC_DESTINATION_PREFIX)
        .setRelayHost(host)
        .setClientLogin(username)
        .setClientPasscode(password)
        .setSystemLogin(username)
        .setSystemPasscode(password);
  }
}
