package ru.alferatz.ftserver.auth;


import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "hse-auth", url = "${client.hse.url}")
@Headers("Transfer-Encoding: chunked")
public interface HseAuthClient {

  @PostMapping("/adfs/oauth2/authorize")
  String auth(/*@RequestHeader("Transfer-Encoding") String header,*/
      @RequestParam("response_type") String responseType,
      @RequestParam("client_id") String clientId,
      @RequestParam("redirect_uri") String redirectUrl);
}
