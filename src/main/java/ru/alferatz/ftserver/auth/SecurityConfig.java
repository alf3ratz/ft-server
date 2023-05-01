//package ru.alferatz.ftserver.auth;
//
//import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC;
//import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
//import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.POST;
//
//import java.util.Arrays;
//import java.util.List;
//import javax.servlet.FilterChain;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.FormHttpMessageConverter;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
//import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
//import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
//import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
//import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
//import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.cors.CorsConfiguration;
//
//@Configuration
//@EnableOAuth2Client
////@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
////@Import({OauthRestTemplateConfig.class})
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//  @EnableWebSecurity
//  public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//      http.httpBasic().disable()
//          .formLogin().disable();
////      http
////          .authorizeRequests().anyRequest().permitAll()
////          .and()
////          .formLogin().loginPage("/login").permitAll()
////          .and()
////          .csrf().disable();
////      http
////          .authorizeRequests().and().login
//
////          .oauth2Login(
////              oauth2Login ->
////                  oauth2Login
////                      .redirectionEndpoint(redirectionEndpoint ->
////                          redirectionEndpoint
////                              .baseUri("/api/auth/redirect_hse")
////                      )
//      //Customizer.withDefaults()
////          );
//    }
//  }
////  @Bean
////  public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
////    OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
////    idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> MacAlgorithm.HS256);
////    return idTokenDecoderFactory;
////  }
////  @Bean
////  public ClientRegistration clientRegistration() {
////    return ClientRegistration
////        .withRegistrationId("hse")
////        .clientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
////        .clientAuthenticationMethod(BASIC)
////        .authorizationGrantType(AuthorizationGrantType.IMPLICIT)
//////        .userInfoUri("https://api.bitbucket.org/2.0/user")
////        //.tokenUri("https://auth.hse.ru/adfs/oauth2/authorize")
////        .authorizationUri("https://auth.hse.ru/adfs/oauth2/authorize?response_type=token&client_id=fe0df921-754d-45e8-8d48-1fcef2d91df8&redirect_uri=https://ftapp.herokuapp.com/auth/hse_redirect")
////        .redirectUri("https://ftapp.herokuapp.com/auth/hse_redirect")
////        //.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
////        .build();
////  }
////
////
////  @Bean
////  @Autowired
////  public ClientRegistrationRepository clientRegistrationRepository(
////      List<ClientRegistration> registrations) {
////    return new InMemoryClientRegistrationRepository(registrations);
////  }
//
//
//}
////  @Autowired
////  private CustomOAuth2UserService customOAuth2UserService;
////
////  @Override
////  protected void configure(HttpSecurity http) throws Exception {
//////    CorsConfiguration corsConfiguration = new CorsConfiguration();
//////    http.httpBasic().disable()
//////        .formLogin().disable();
////    http
////        .authorizeRequests()
////        .antMatchers("/login.html").permitAll()
////        .anyRequest()
////        .authenticated()
////        .and()
////        .oauth2Login()
//////        .loginPage("/login.html")
//////        .defaultSuccessUrl("/success.html");
////    .tokenEndpoint()
////        .accessTokenResponseClient(accessTokenResponseClient())
////        //Userinfo endpoint
////        .and()
////        .userInfoEndpoint()
////        .userService(customOAuth2UserService);
////  }
//
////  @Bean
////  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
////    DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
////        new DefaultAuthorizationCodeTokenResponseClient();
////    OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter =
////        new OAuth2AccessTokenResponseHttpMessageConverter();
////    tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());
////    RestTemplate restTemplate = new RestTemplate(Arrays.asList(
////        new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
////    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
////    accessTokenResponseClient.setRestOperations(restTemplate);
////    return accessTokenResponseClient;
////  }
////  public class OAuth2AuthorizationRequestRedirectFilter {
////    void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
////      String registrationId = resolveRegistrationId(request);
////      OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request);
////      if (authorizationRequest != null) {
////        authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
////        authorizationRedirectStrategy.sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
////      } else {
////        filterChain.doFilter(request, response);
////      }
////    }
////  }
////
////  public DefaultOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
////    OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
////      ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
////      OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode();
////      builder
////          .clientId(clientRegistration.getClientId())
////          .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
////          .redirectUri(redirectUriStr)
////          .scopes(clientRegistration.getScopes())
////          .state(this.stateGenerator.generateKey())
////          .attributes(attributes);
////      return builder.build();
////    }
////  }
////  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////    http
////        // ваши настройки +
////        .oauth2Client();
////    return http.build();
////  }
////https://auth.hse.ru/adfs/oauth2/authorize?response_type=token&client_id=fe0df921-754d-45e8-8d48-1fcef2d91df8&redirect_uri=https://ftapp.herokuapp.com/auth/hse_redirect&response_mode=query
//
