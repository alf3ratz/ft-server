package ru.alferatz.ftserver.service;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.TokenExpiredException;
import ru.alferatz.ftserver.exceptions.UserAlreadyExistException;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.model.VerificationToken;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.VerificationTokenRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final VerificationTokenRepository tokenRepository;
  private final MessageSource messages;
  private final JavaMailSender mailSender;

  public UserEntity login(UserDto userDto) {
    if (!isUserInfoValid(userDto)) {
      throw new BadRequestException("Отсутствует информация для валидации пользователя");
    }
    // Создаем пользователя, если его нет в базе
    UserEntity userEntity = userRepository.getUserEntityByEmail(userDto.getEmail()).orElse(null);
    if (userEntity == null) {
      userEntity = UserEntity.builder()
          .email(userDto.getEmail())
          .username(userDto.getUsername())
          .build();
      try {
        userRepository.save(userEntity);
      } catch (RuntimeException ex) {
        throw new InternalServerError(
            String.format("Не удалось сохранить пользователя в базе: %s", ex.getMessage()));
      } finally {
        userRepository.flush();
      }
    }
    String token = UUID.randomUUID().toString();
    // Сохраняем токен
    createVerificationToken(userEntity, token);

    String recipientAddress = userEntity.getEmail();
    String subject = "Registration Confirmation";
    String confirmationUrl
        = "/loginConfirm?token=" + token;
    //String message = messages.getMessage("message.regSucc", null, Locale.ENGLISH);

    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText("blabla" + "\r\n" + "https://ftapp.herokuapp.com" + confirmationUrl);
    try {
      mailSender.send(email);
      return userEntity;
    } catch (MailException ex) {
      throw new InternalServerError(ex.getMessage());
    }
  }

  public HttpStatus confirmRegistration(String token) {
    Locale locale = Locale.ENGLISH;

    VerificationToken verificationToken = getVerificationToken(token);
    if (verificationToken == null) {
      throw new BadRequestException("Неверный код подтверждения");
    }
    UserEntity user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      throw new TokenExpiredException("Обновите код подтверждения");
    }

    //user.setEnabled(true);
    saveRegisteredUser(user);
    return HttpStatus.OK;
  }

  private boolean isUserInfoValid(UserDto userDto) {
    return !userDto.getUsername().isEmpty() && !userDto.getEmail().isEmpty();
  }

  public UserEntity getUser(String verificationToken) {
    UserEntity user = tokenRepository.findByToken(verificationToken).getUser();
    return user;
  }

  public VerificationToken getVerificationToken(String VerificationToken) {
    return tokenRepository.findByToken(VerificationToken);
  }

  public void saveRegisteredUser(UserEntity user) {
    userRepository.save(user);
  }

  public void createVerificationToken(UserEntity user, String token) {
    VerificationToken myToken = new VerificationToken(token, user);
    myToken.setExpiryDate(myToken.calculateExpiryDate());
    tokenRepository.save(myToken);
  }
}
