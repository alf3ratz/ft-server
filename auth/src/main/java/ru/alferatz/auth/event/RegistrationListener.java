package ru.alferatz.auth.event;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ru.alferatz.auth.service.AuthService;
import ru.alferatz.travels.repository.entity.UserEntity;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements
    ApplicationListener<OnRegistrationCompleteEvent> {

  private final AuthService service;

  private final MessageSource messages;

  private JavaMailSender mailSender;

  @Override
  public void onApplicationEvent(OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(OnRegistrationCompleteEvent event) {
    UserEntity user = event.getUser();
    String token = UUID.randomUUID().toString();
    service.createVerificationToken(user, token);

    String recipientAddress = user.getEmail();
    String subject = "Registration Confirmation";
    String confirmationUrl
        = event.getAppUrl() + "/regitrationConfirm?token=" + token;
    String message = messages.getMessage("message.regSucc", null, event.getLocale());

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
    mailSender.send(email);
  }
}