package ru.alferatz.auth.controller;

import java.util.Calendar;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.alferatz.auth.error.UserAlreadyExistException;
import ru.alferatz.auth.event.OnRegistrationCompleteEvent;
import ru.alferatz.auth.model.VerificationToken;
import ru.alferatz.auth.service.AuthService;
import ru.alferatz.travels.model.UserDto;
import ru.alferatz.travels.repository.entity.UserEntity;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

  private final ApplicationEventPublisher eventPublisher;
  private final AuthService userService;
  private final MessageSource messages;

  @PostMapping("/registration")
  public ModelAndView registerUserAccount(
      @ModelAttribute("user") @Valid UserDto userDto,
      HttpServletRequest request, Errors errors) {

    try {
      UserEntity registered = userService.registerNewUserAccount(userDto);

      String appUrl = request.getContextPath();
      eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
          request.getLocale(), appUrl));
    } catch (UserAlreadyExistException uaeEx) {
      ModelAndView mav = new ModelAndView("registration", "user", userDto);
      mav.addObject("message", "An account for that username/email already exists.");
      return mav;
    } catch (RuntimeException ex) {
      return new ModelAndView("emailError", "user", userDto);
    }

    return new ModelAndView("successRegister", "user", userDto);
  }

  @GetMapping("/regitrationConfirm")
  public String confirmRegistration
      (WebRequest request, Model model, @RequestParam("token") String token) {

    Locale locale = request.getLocale();

    VerificationToken verificationToken = userService.getVerificationToken(token);
    if (verificationToken == null) {
      String message = messages.getMessage("auth.message.invalidToken", null, locale);
      model.addAttribute("message", message);
      return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    UserEntity user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      String messageValue = messages.getMessage("auth.message.expired", null, locale);
      model.addAttribute("message", messageValue);
      return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    //user.setEnabled(true);
    userService.saveRegisteredUser(user);
    return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
  }
}
