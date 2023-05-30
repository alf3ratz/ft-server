package ru.alferatz.ftserver.auth;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.request.AuthResponse;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  private final AuthService authService;
  private final Map<String, Pair<String,String>> emailToSession = new HashMap<>();

  @GetMapping("/auth/hse_redirect")
  public void redirect(@RequestParam("code") String token) {
//    try{
//      authService.redirect(token);
//    }catch (RuntimeException ex){
//      throw new InternalServerError("blabla");
//    }
  }

  @PostMapping("/auth/login")
  public void login() {
    //authService.getCode();
  }

  @PostMapping("/oauth2/authorization/hse")
  public void check() {
    System.out.println("был тут");
  }

  @RequestMapping("/auth/success")
  public String success() {
    SecurityContext context = SecurityContextHolder.getContext();
    var authDetails = (WebAuthenticationDetails) context.getAuthentication().getDetails();
    var credentials = (MyOAuth2User) context.getAuthentication().getPrincipal();
    var credMap = credentials.getAttributes();
    var userName = ((String) credMap.get("username"));
    var sessionId = authDetails.getSessionId();
    //var res = new ObjectMapper().readValue(username, HashMap.class);
    emailToSession.put(sessionId, Pair.of((String) credMap.get("email"),(String) credMap.get("username")));
    return String
        .format("%s successfully authorized!\nSave value and close the window\nValue: %s",
            userName, sessionId);
  }

  @GetMapping("/auth/isLogged")
  public AuthResponse isSuccessfullyLogged(@RequestParam String sessionId) {
    if(emailToSession.containsKey(sessionId)){
      return AuthResponse.builder()
          .isLogged(emailToSession.containsKey(sessionId))
          .email(emailToSession.get(sessionId).getLeft())
          .username(emailToSession.get(sessionId).getRight())
          .build();
    }
    return AuthResponse.builder()
        .isLogged(emailToSession.containsKey(sessionId))
        .build();
  }

}
