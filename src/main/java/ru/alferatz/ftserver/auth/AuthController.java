package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  private final AuthService authService;

  @GetMapping("/auth/hse_redirect")
  public void redirect(@RequestParam("code") String token) {
    authService.redirect(token);
  }

  @PostMapping("/login")
  public void login() {
    authService.getCode();
  }

  @GetMapping("/oauth2/authorization/hse")
  public void check() {
    System.out.println("был тут");
  }


}
