package ru.alferatz.auth.event;

import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import ru.alferatz.travels.repository.entity.UserEntity;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private String appUrl;
  private Locale locale;
  private UserEntity user;

  public OnRegistrationCompleteEvent(
      UserEntity user, Locale locale, String appUrl) {
    super(user);

    this.user = user;
    this.locale = locale;
    this.appUrl = appUrl;
  }

  // standard getters and setters
}
