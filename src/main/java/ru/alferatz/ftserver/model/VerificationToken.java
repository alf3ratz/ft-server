package ru.alferatz.ftserver.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Entity
@Getter
@Setter
@Table(name = "verification_token_jn")
@NoArgsConstructor
public class VerificationToken {

  private static final int EXPIRATION = 60 * 24;

  public VerificationToken(String token, UserEntity user) {
    this.token = token;
    this.user = user;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "user_id")
  private UserEntity user;

  private Date expiryDate;

  public Date calculateExpiryDate() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    cal.add(Calendar.MINUTE, EXPIRATION);
    return new Date(cal.getTime().getTime());
  }

  // standard constructors, getters and setters
}
