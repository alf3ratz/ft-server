package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
//@EnableOAuth2Client
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
//@Import({OauthRestTemplateConfig.class})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    http.httpBasic().disable()
        .formLogin().disable();
  }
//  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//        // ваши настройки +
//        .oauth2Client();
//    return http.build();
//  }
//https://auth.hse.ru/adfs/oauth2/authorize?response_type=token&client_id=fe0df921-754d-45e8-8d48-1fcef2d91df8&redirect_uri=https://ftapp.herokuapp.com/auth/hse_redirect
}
