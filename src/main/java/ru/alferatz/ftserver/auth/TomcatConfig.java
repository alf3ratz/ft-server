//package ru.alferatz.ftserver.auth;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//
//  @Value("${server.http.port}")
//  private int httpPort;
//
//  @Bean
//  public EmbeddedServletContainerCustomizer containerCustomizer() {
//    return new EmbeddedServletContainerCustomizer() {
//      @Override
//      public void customize(ConfigurableEmbeddedServletContainer container) {
//        if (container instanceof TomcatEmbeddedServletContainerFactory) {
//          TomcatEmbeddedServletContainerFactory containerFactory =
//              (TomcatEmbeddedServletContainerFactory) container;
//
//          Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
//          connector.setPort(httpPort);
//          containerFactory.addAdditionalTomcatConnectors(connector);
//        }
//      }
//    };
//  }
//}