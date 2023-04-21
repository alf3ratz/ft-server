package ru.alferatz.ftserver.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@Configuration
//@Import({PropertiesConfig.class}) //Imports properties from properties files.
public class OauthRestTemplateConfig {



  @Bean
  public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext oauth2ClientContext) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(oauth2ResourceDetails(), oauth2ClientContext);
    return template;
  }

  @Bean
  OAuth2ProtectedResourceDetails oauth2ResourceDetails() {
    AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
    details.setId("theOauth");
    details.setClientId("fe0df921-754d-45e8-8d48-1fcef2d91df8");
    //details.setClientSecret("SecretKey");
    //details.setAccessTokenUri("https://theAuthenticationServer.com/oauthserver/oauth2/token");
    details.setUserAuthorizationUri("https://auth.hse.ru/adfs/oauth2/authorize");
    details.setTokenName("accessToken");
    details.setPreEstablishedRedirectUri("https://ftapp.herokuapp.com/auth/hse_redirect");
   // details.setUseCurrentUri(true);
    return details;//https://ftapp.herokuapp.com/auth/hse_redirect
  }
}