package ru.alferatz.ftserver.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GttOAuth2UserService implements
    ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {

  @Override
  public Mono<OAuth2User> loadUser(OAuth2UserRequest oAuth2UserRequest)
      throws OAuth2AuthenticationException {
    final List<GrantedAuthority> authorities = Arrays
        .asList(new SimpleGrantedAuthority("authority"));
    final Map<String, Object> attributes = oAuth2UserRequest.getAdditionalParameters();
    final OAuth2User user = new DefaultOAuth2User(authorities, attributes, "email");
    return Mono.just(user);
  }
}
