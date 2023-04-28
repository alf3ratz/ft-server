//package ru.alferatz.ftserver.auth;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.keys.KeyManager;
//import org.springframework.security.crypto.keys.StaticKeyGeneratingKeyManager;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
//import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
//import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
////@EnableWebSecurity
////@Import(OAuth2AuthorizationServerConfiguration.class)
////public class AuthorizationServerConfig {
////  // @formatter:off
////  @Bean
////  public RegisteredClientRepository registeredClientRepository() {
////    RegisteredClient registeredClient = RegisteredClient.withId("hse")
////        .clientId("fe0df921-754d-45e8-8d48-1fcef2d91df8") // пункт 6
////        //.clientSecret("secret")       //пункт 7
////        .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
////        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)// пункт 2
////        //.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
////        .redirectUri("https://ftapp.herokuapp.com/auth/hse_redirect") //пункт 3
////        //.scope("message.read") //пункт 8
////        //.scope("message.write") //пункт 8
////        .clientSettings(clientSettings -> clientSettings.requireUserConsent(true))
////        .build();
////    return new InMemoryRegisteredClientRepository(registeredClient);
////  }
////  @Bean
////  public KeyManager keyManager() {
////    return new StaticKeyGeneratingKeyManager();
////  }
////  // @formatter:off
////  @Bean
////  public UserDetailsService users() {
////    UserDetails user = User.withDefaultPasswordEncoder()
////        .username("user1")
////        .password("password")
////        .roles("USER")
////        .build();
////    return new InMemoryUserDetailsManager(user);
////  }
////}
