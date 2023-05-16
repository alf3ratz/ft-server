package ru.alferatz.ftserver.auth;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
public class SecurityConfig {

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
              withDefaults()
          );
//      http
//          .oauth2Login(oauth2Login ->
//              oauth2Login
//                  .clientRegistrationRepository(this.clientRegistrationRepository())
//                  .authorizedClientRepository(this.authorizedClientRepository())
//                  .authorizedClientService(this.authorizedClientService())
//                  .loginPage("/login")
//                  .authorizationEndpoint(authorizationEndpoint ->
//                      authorizationEndpoint
//                          .baseUri(this.authorizationRequestBaseUri())
//                          .authorizationRequestRepository(this.authorizationRequestRepository())
//                          .authorizationRequestResolver(this.authorizationRequestResolver())
//                  )
//                  .redirectionEndpoint(redirectionEndpoint ->
//                      redirectionEndpoint
//                          .baseUri("/auth/hse_redirect")
//                  )
//                  .tokenEndpoint(tokenEndpoint ->
//                      tokenEndpoint
//                          .accessTokenResponseClient(this.accessTokenResponseClient())
//                  )
//                  .userInfoEndpoint(userInfoEndpoint ->
//                      userInfoEndpoint
//                          .userAuthoritiesMapper(this.userAuthoritiesMapper())
//                          .userService(this.oauth2UserService())
//                          .oidcUserService(this.oidcUserService())
//                          .customUserType(GitHubOAuth2User.class, "github")
//                  )
//          );
    }
  }

//  @Bean
//  public ClientRegistrationRepository clientRegistrationRepository() {
//    return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//  }

  @Bean
  public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
    OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
    idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> MacAlgorithm.HS256);
    return idTokenDecoderFactory;
  }

  @Autowired
  private ResourceServerProperties sso;

  @Bean
  public ResourceServerTokenServices userInfoTokenServices() {
    return new AdfsUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
  }

//  private ClientRegistration googleClientRegistration() {
//
//    return ClientRegistration.withRegistrationId("hse")
//        .clientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
//        //.clientSecret("google-client-secret")
//        //.clientAuthenticationMethod(ClientAuthenticationMethod)
//        //.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//        .redirectUri("https://www.ft-app.online/auth/hse_redirect")
//        //.redirectUriTemplate("https://ftapp.herokuapp.com/auth/hse_redirect")
//        //.scope("openid", "profile", "email", "address", "phone")
//        .authorizationUri("https://auth.hse.ru/adfs/oauth2/authorize")
//        .tokenUri("https://auth.hse.ru/adfs/oauth2/token")
//        // .clientAuthenticationMethod(ClientAuthenticationMethod.PRIVATE_KEY_JWT)
//        .userInfoUri("https://auth.hse.ru/adfs/oauth2/token")
//        //.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//        //.userNameAttributeName(IdTokenClaimNames.SUB)
//        //.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
//        .clientName("hse")
//        .build();
//  }

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .refreshToken()
            .clientCredentials()
            .password()
            .build();

    DefaultOAuth2AuthorizedClientManager authorizedClientManager =
        new DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }
}