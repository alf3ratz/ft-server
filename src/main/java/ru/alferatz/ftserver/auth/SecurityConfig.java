package ru.alferatz.ftserver.auth;

import java.util.List;
import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
@Import({OauthRestTemplateConfig.class})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final OAuth2ClientContext oauth2ClientContext;
  private static final String[] AUTH_LIST = {
      "/v3/api-docs/**", "/configuration/ui", "/swagger-resources/**", "/configuration/security",
      "/swagger-ui/**", "/webjars/**", "/", "/login**", "/error**","/api/auth/login"
  };

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
    corsConfiguration.setAllowedOrigins(List.of("*"));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setExposedHeaders(List.of("Authorization"));
    http.authorizeRequests().antMatchers(AUTH_LIST).permitAll().anyRequest()
        .authenticated()
        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
        //.and().httpBasic().disable();
        //.and().addFilterBefore(oauth2ClientFilter(), BasicAuthenticationFilter.class);
    http
        .addFilterBefore(new OAuth2ClientContextFilter(), BasicAuthenticationFilter.class)
        .addFilterAfter(oauth2ClientAuthenticationProcessingFilter(), OAuth2ClientContextFilter.class)
    ;
  }

  @Bean
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  private final OAuth2RestTemplate oAuth2RestTemplate;

//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//
//    http
//        .authorizeRequests()
//        .accessDecisionManager(accessDecisionManager()) //This is a WebExpressionVoter. I don't think it's related to the problem so didn't include the source.
//        .antMatchers("/login").permitAll()
//        .antMatchers("/api/**").authenticated()
//        .anyRequest().authenticated();
//    http
//        .exceptionHandling()
//        .authenticationEntryPoint(delegatingAuthenticationEntryPoint());
//    http
//        .addFilterBefore(new OAuth2ClientContextFilter(), BasicAuthenticationFilter.class)
//        .addFilterAfter(oauth2ClientAuthenticationProcessingFilter(), OAuth2ClientContextFilter.class)
//    ;
//  }

  private OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthenticationProcessingFilter() {
    OAuth2ClientAuthenticationProcessingFilter
        daFilter = new OAuth2ClientAuthenticationProcessingFilter("/api/**");
    daFilter.setRestTemplate(oAuth2RestTemplate);
    daFilter.setTokenServices(inMemoryTokenServices());
    return daFilter;
  }

  private DefaultTokenServices inMemoryTokenServices() {
    InMemoryTokenStore tok = new InMemoryTokenStore();
    DefaultTokenServices tokenService = new DefaultTokenServices();
    tokenService.setTokenStore(tok);

    return tokenService;
  }






//  @Bean
//  @ConfigurationProperties("spring.security.oauth2.client")
//  public AuthorizationCodeResourceDetails githubClient() {
//    return new AuthorizationCodeResourceDetails();
//  }
//
//  @Bean
//  @ConfigurationProperties("github.resource")
//  public ResourceServerProperties githubResource() {
//    return new ResourceServerProperties();
//  }
//
//  @Bean
//  public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
//      OAuth2ClientContextFilter filter) {
//    FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
//    registration.setFilter(filter);
//    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
//    return registration;
//  }
//
//  private Filter oauth2ClientFilter() {
//    OAuth2ClientAuthenticationProcessingFilter oauth2ClientFilter = new OAuth2ClientAuthenticationProcessingFilter(
//        "/api/auth/login");
//    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(githubClient(), oauth2ClientContext);
//    oauth2ClientFilter.setRestTemplate(restTemplate);
//    UserInfoTokenServices tokenServices = new UserInfoTokenServices(
//        githubResource().getUserInfoUri(),
//        githubClient().getClientId());
//    tokenServices.setRestTemplate(restTemplate);
//    oauth2ClientFilter.setTokenServices(tokenServices);
//    return oauth2ClientFilter;
//  }
}