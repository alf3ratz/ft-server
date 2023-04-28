package ru.alferatz.ftserver.auth;

import java.io.IOException;
import javax.management.RuntimeErrorException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

  //  private final HseAuthService hseAuthService;
  private final UserRepository userRepository;

  @GetMapping("/authWithHse")
  public String authWithHse() {
    //return hseAuthService.authorize();
//    try {
//      OAuthClientRequest request = OAuthClientRequest
//         // .setGrantType(GrantType.IMPLICIT)
//          .authorizationLocation("https://auth.hse.ru/adfs/oauth2/authorize")
//          .setClientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
//          .setRedirectURI("https://ftapp.herokuapp.com/auth/hse_redirect")
//          .setResponseType(ResponseType.CODE.toString())
//          .buildQueryMessage();
//      OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
//      var request = new HttpServletRequest();
//      OAuthAuthzResponse oar = null;
//      oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
//
//      // Get Authorization Code
//      return oar.getCode();
//    } catch (OAuthSystemException | OAuthProblemException e) {
//      throw new InternalServerError(e.getMessage());
//    }
    return "blabla";
  }

  @GetMapping("/authorize")
  public String authorize(
      HttpServletRequest req,
      HttpServletResponse res)
      throws OAuthSystemException, IOException {

    //logger.debug("start processing /authorize request");

    try {

//      res.addCookie(new Cookie("clientId", oauthParams.getClientId()));
//      res.addCookie(new Cookie("clientSecret", oauthParams.getClientSecret()));
//      res.addCookie(new Cookie("authzEndpoint", oauthParams.getAuthzEndpoint()));
//      res.addCookie(new Cookie("tokenEndpoint", oauthParams.getTokenEndpoint()));
//      res.addCookie(new Cookie("redirectUri", oauthParams.getRedirectUri()));
//      res.addCookie(new Cookie("scope", oauthParams.getScope()));
//      res.addCookie(new Cookie("state", oauthParams.getState()));
//      res.addCookie(new Cookie("app", oauthParams.getApplication()));

      OAuthClientRequest request = OAuthClientRequest
          .authorizationLocation("https://auth.hse.ru/adfs/oauth2/authorize")
          .setClientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
          .setRedirectURI("https://ftapp.herokuapp.com/auth/hse_redirect")
          .setResponseType(ResponseType.CODE.toString())
//          .setScope(oauthParams.getScope())
//          .setState(oauthParams.getState())
          .buildQueryMessage();

      var qw = new ModelAndView(new RedirectView(request.getLocationUri()));
      return "das";
    } catch (RuntimeErrorException e) {
      throw new InternalServerError(e.getMessage());
    }
  }

  @RequestMapping("/foo")
  void handleFoo(HttpServletResponse response) throws IOException, OAuthSystemException {
    OAuthClientRequest request = OAuthClientRequest
        .authorizationLocation("https://auth.hse.ru/adfs/oauth2/authorize")
        .setClientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
        .setRedirectURI("https://ftapp.herokuapp.com/auth/hse_redirect")
        .setResponseType(ResponseType.TOKEN.toString())
//          .setScope(oauthParams.getScope())
//          .setState(oauthParams.getState())
        .buildQueryMessage();
    response.sendRedirect(request.getLocationUri());
  }
//  @RequestMapping(value = "/redirect", method = RequestMethod.GET)
//  public ModelAndView handleRedirect(
//      HttpServletRequest request,
//      HttpServletResponse response){
//    try {
//      OAuthClientRequest req = OAuthClientRequest
//          // .setGrantType(GrantType.IMPLICIT)
//          .authorizationLocation("https://auth.hse.ru/adfs/oauth2/authorize")
//          .setClientId("fe0df921-754d-45e8-8d48-1fcef2d91df8")
//          .setRedirectURI("https://ftapp.herokuapp.com/auth/hse_redirect")
//          .setResponseType(ResponseType.CODE.toString())
//          .buildQueryMessage();
//      OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
//      var request = new HttpServletRequest();
//      OAuthAuthzResponse oar = null;
//      oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
//
//      // Get Authorization Code
//      return oar.getCode();
//    } catch (OAuthSystemException e) {
//      throw new InternalServerError(e.getMessage());
//    }
//  }


  @GetMapping("/redirect_hse")
  public void redirectCallback(@RequestParam("access_token") String accessToken) {
    var usr = UserEntity.builder()
        .travelId(1L)
        .chatId(1L)
        .email(accessToken.substring(0, 48))
        .build();
    userRepository.saveAndFlush(usr);
  }
}
