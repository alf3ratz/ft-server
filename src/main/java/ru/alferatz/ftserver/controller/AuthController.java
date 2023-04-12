package ru.alferatz.ftserver.controller;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.TokenExpiredException;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.model.VerificationToken;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.service.AuthService;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  private final ApplicationEventPublisher eventPublisher;
  private final AuthService authService;

  @PostMapping("/login")
  public UserEntity login(@RequestBody @Valid UserDto userDto) {
    return authService.login(userDto);
//    try {
//      UserEntity registered = userService.registerNewUserAccount(userDto);
//
//      String appUrl = request.getContextPath();
//      eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
//          request.getLocale(), appUrl));
//    } catch (UserAlreadyExistException uaeEx) {
//      ModelAndView mav = new ModelAndView("registration", "user", userDto);
//      mav.addObject("message", "An account for that username/email already exists.");
//      return mav;
//    } catch (RuntimeException ex) {
//      return new ModelAndView("emailError", "user", userDto);
//    }

    // return new ModelAndView("successRegister", "user", userDto);
  }

  @GetMapping("/loginConfirm")
  public HttpStatus confirmRegistration(@RequestParam("token") String token) {
    return authService.confirmRegistration(token);
  }
}
