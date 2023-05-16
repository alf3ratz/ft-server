package ru.alferatz.ftserver.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RestTemplateClient client;
  private final UserRepository userRepository;

  public void redirect(String token) {
    var result = client.callRestClient(token);
    ObjectNode objectNode = result.getBody().deepCopy();
    var tok = objectNode.get("accessToken");
    var res = tok.toString();
    UserEntity userEntity = UserEntity.builder()
        .username(token.substring(0, 20))
        .email(res.substring(20, 40))
        .build();
    userRepository.save(userEntity);
    userRepository.flush();
  }
//  private static void getEmployees()
//  {
//    final String uri = "https://auth.hse.ru/adfs/oauth2/token";
//
//    RestTemplate restTemplate = new RestTemplate();
//    String result = restTemplate.getForObject(uri, String.class);
//
//    System.out.println(result);
//  }

  public void auth() {
    String clientId = "fe0df921-754d-45e8-8d48-1fcef2d91df8";
    //String clientSecret = "zJpDtrqk7is9OwjDNWi5CzOK";
    String callbackUri = "https://www.ft-app.online/auth/hse_redirect";
  }
}
