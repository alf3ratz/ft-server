package ru.alferatz.ftserver.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final RestTemplateClient client;
  private final UserRepository userRepository;
  private WebClient webClient;
  private String tokenUrl = "https://auth.hse.ru/adfs/oauth2/token";
  private String authUrl = "https://auth.hse.ru/adfs/oauth2/authorize";
  private final String clientId = "fe0df921-754d-45e8-8d48-1fcef2d91df8";
  private final String redirect_uri = "https://www.ft-app.online/auth/hse_redirect";

  public void getCode() {
    webClient = WebClient.builder()
        .baseUrl("http://www.google.com/")
        .exchangeStrategies(ExchangeStrategies.withDefaults())
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create().followRedirect(true)
        ))
        .build();
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    //map.add("token", token);
    map.add("client_id", clientId);
    map.add("redirect_uri", redirect_uri);
    String response = null;
    try {
      response = webClient.post()
          .uri(new URI("https://auth.hse.ru/adfs/oauth2/authorize"))
          //.header("Authorization", "Bearer MY_SECRET_TOKEN")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          //.accept(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromFormData(map))
          .retrieve()
          .bodyToMono(String.class)
          .block();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    System.out.println(response);
  }

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
