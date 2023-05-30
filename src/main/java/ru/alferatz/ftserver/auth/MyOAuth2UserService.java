package ru.alferatz.ftserver.auth;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static lombok.Lombok.checkNotNull;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.service.UserService;

public class MyOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

  private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

  private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

  private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
  };

  private final UserService userService;

  private final RestOperations restOperations;

  private final Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();

  public MyOAuth2UserService(UserService userService) {
    this.userService = requireNonNull(userService);
    this.restOperations = createRestTemplate();
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    var token = userRequest.getAccessToken().getTokenValue();
    String[] chunks = token.split("\\.");
    Base64.Decoder decoder = Base64.getUrlDecoder();

    String header = new String(decoder.decode(chunks[0]));
    String payload = new String(decoder.decode(chunks[1]));
    Map<String, Object> result = null;
    UserEntity user = null;
    try {
      result =
          new ObjectMapper().readValue(payload, HashMap.class);
      var name = result.get("given_name");
      var lastName = result.get("family_name");
      var email = result.get("email");
      user = findOrCreate(String.valueOf(name), String.valueOf(lastName), String.valueOf(email));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
//    checkNotNull(userRequest, "userRequest cannot be null");
//    if (!StringUtils
//        .hasText(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
//            .getUri())) {
//      OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
//          "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
//              + userRequest.getClientRegistration().getRegistrationId(), null);
//      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
//    }
//
//    String registrationId = userRequest.getClientRegistration().getRegistrationId();
//    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
//        .getUserInfoEndpoint().getUserNameAttributeName();
//    if (!StringUtils.hasText(userNameAttributeName)) {
//      OAuth2Error oauth2Error = new OAuth2Error(
//          MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
//          "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
//              + registrationId, null);
//      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
//    }
//
//    ResponseEntity<Map<String, Object>> response;
//    try {
//      // OAuth2UserRequestEntityConverter cannot return null values.
//      //noinspection ConstantConditions
//      response = this.restOperations
//          .exchange(requestEntityConverter.convert(userRequest), PARAMETERIZED_RESPONSE_TYPE);
//    } catch (OAuth2AuthorizationException ex) {
//      OAuth2Error oauth2Error = ex.getError();
//      StringBuilder errorDetails = new StringBuilder();
//      errorDetails.append("Error details: [");
//      errorDetails.append("UserInfo Uri: ").append(
//          userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
//      errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
//      if (oauth2Error.getDescription() != null) {
//        errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
//      }
//      errorDetails.append("]");
//      oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
//          "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails
//              .toString(), null);
//      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
//    } catch (RestClientException ex) {
//      OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
//          "An error occurred while attempting to retrieve the UserInfo Resource: " + ex
//              .getMessage(), null);
//      throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
//    }

    Map<String, Object> userAttributes = new HashMap();//.ofEntries(entry("username", new Object()),entry("display_name",new Object()));//emptyIfNull(response.getBody());
    userAttributes.put("username", user.getUsername());
    userAttributes.put("email", user.getEmail());
    Set<GrantedAuthority> authorities = new LinkedHashSet<>();
    authorities.add(new OAuth2UserAuthority(userAttributes));

    for (String authority : userRequest.getAccessToken().getScopes()) {
      authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
    }
    var userNameAttributeName = user.getUsername();
    // ищем пользователя в нашей БД, либо создаем нового
    // если пользователь не найден и система не подразумевает автоматической регистрации,
    // необходимо сгенерировать тут исключение
    //UserEntity user = findOrCreate(userAttributes);
    userAttributes.put(MyOAuth2User.ID_ATTR, user.getId());
    return new MyOAuth2User(userNameAttributeName, userAttributes, authorities);
  }

  private UserEntity findOrCreate(String name, String lastName, String email) {
    Optional<UserEntity> userOpt = userService.findByEmail(email);
    UserEntity updatedUser = null;

    if (userOpt.isEmpty()) {
      UserEntity user = UserEntity.builder()
          .username(String.format("%s %s", lastName, name))
          .email(email)
          .lastLogged(LocalDateTime.now())
          .build();
      return userService.create(user);
    } else {
      updatedUser = userOpt.get();
      updatedUser.setLastLogged(LocalDateTime.now());
    }
    return updatedUser;
  }

  private RestTemplate createRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
    //return restTemplate;
//    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
//    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
//    messageConverters.add(converter);
//    restTemplate.setMessageConverters(messageConverters);
    restTemplate.getInterceptors().add((httpRequest, bytes, clientHttpRequestExecution) -> {
      ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
      String text = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
      System.out.println(text);
      return response;
    });
    return restTemplate;
  }
}