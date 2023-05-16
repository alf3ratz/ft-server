package ru.alferatz.ftserver.auth;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestTemplateClient {

  //@Value("${rest.client.url}")
  private String url = "https://auth.hse.ru/adfs/oauth2/token";

  private final RestTemplate restTemplate;

  public ResponseEntity<JsonNode> callRestClient(String token) {

    //String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.Jf36POk6yJV_adQssw5c";
    String clientId = "fe0df921-754d-45e8-8d48-1fcef2d91df8";
    String redirect_uri = "https://www.ft-app.online/auth/hse_redirect";
    String grant_type = "authorization_code";
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("token", token);
    map.add("client_id", clientId);
    map.add("redirect_uri", redirect_uri);
    map.add("grant_type", grant_type);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map,
        headers);
    ResponseEntity<JsonNode> responseEntity = null;
    try {
      responseEntity = restTemplate.postForEntity(url, requestBodyFormUrlEncoded, JsonNode.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return responseEntity;
  }

}