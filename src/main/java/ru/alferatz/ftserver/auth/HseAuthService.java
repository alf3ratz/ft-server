package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class HseAuthService {

  @Autowired
  private HseAuthClient hseAuthClient;

  public String authorize() {
    //?response_type=token&client_id=fe0df921-754d-45e8-8d48-1fcef2d91df8&redirect_uri=https://ftapp.herokuapp.com/auth/hse_redirect
    String responseType = "response_type";
    String clientId = "fe0df921-754d-45e8-8d48-1fcef2d91df8";
    String recirectUri = "https://ftapp.herokuapp.com/auth/hse_redirect";
    String header = "chunked";
    String result = hseAuthClient.auth(responseType, clientId, recirectUri);
    return result;
  }

}
