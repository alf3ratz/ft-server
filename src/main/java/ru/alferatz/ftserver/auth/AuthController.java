package ru.alferatz.ftserver.auth;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  @GetMapping("/login")
  public String login(Principal principal) {
      return "";
  }
}
