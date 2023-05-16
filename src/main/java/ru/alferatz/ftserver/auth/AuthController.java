package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  private final AuthService authService;

  @GetMapping("/hse_redirect")
  public void redirect(@RequestParam String token) {
    authService.redirect(token);
  }

}
