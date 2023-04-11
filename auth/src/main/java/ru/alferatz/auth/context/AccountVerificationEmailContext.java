package ru.alferatz.auth.context;

import org.springframework.web.util.UriComponentsBuilder;
import ru.alferatz.travels.repository.entity.UserEntity;

public class AccountVerificationEmailContext extends AbstractEmailContext {

  private String token;


  public <T> void init(T context) {
    //we can do any common configuration setup here
    // like setting up some base URL and context
    UserEntity customer = (UserEntity) context; // we pass the customer informati
    put("firstName", customer.getUsername());
    setTemplateLocation("emails/email-verification");
    setSubject("Complete your registration");
    setFrom("no-reply@javadevjournal.com");
    setTo(customer.getEmail());
  }

  public void setToken(String token) {
    this.token = token;
    put("token", token);
  }

  public void buildVerificationUrl(final String baseURL, final String token) {
    final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
        .path("/register/verify").queryParam("token", token).toUriString();
    put("verificationURL", url);
  }
}
