package ru.alferatz.ftserver.auth;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class OAuth2LoginConfig {

  @EnableWebSecurity
  public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .authorizeRequests(authorizeRequests ->
              authorizeRequests
                  .anyRequest().authenticated()
          )
          .oauth2Login(
              oauth2Login->
                  oauth2Login
                  .redirectionEndpoint(redirectionEndpoint ->
                      redirectionEndpoint
                          .baseUri("/api/auth/redirect_hse")
                    )
          );//(withDefaults());
    }
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
  }

  private ClientRegistration googleClientRegistration() {
    return ClientRegistration.withClientRegistration("hse")
        .clientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
        //.clientSecret("google-client-secret")
        //.clientAuthenticationMethod(ClientAuthenticationMethod)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUriTemplate("https://ftapp.herokuapp.com/auth/hse_redirect")
        //.scope("openid", "profile", "email", "address", "phone")
        .authorizationUri("https://auth.hse.ru/adfs/oauth2/authorize")
        //.tokenUri("https://www.googleapis.com/oauth2/v4/token")
        //.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
        //.userNameAttributeName(IdTokenClaimNames.SUB)
        //.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
        .clientName("hse")
        .build();
  }
}