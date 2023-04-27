package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.TravelDto;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

  private final HseAuthService hseAuthService;

  @GetMapping("/authWithHse")
  public String authWithHse() {
    return hseAuthService.authorize();
  }
  @GetMapping("/redirect_hse")
  public void redirectCallback() {

  }
}
