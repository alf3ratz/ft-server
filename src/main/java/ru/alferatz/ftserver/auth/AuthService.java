package ru.alferatz.ftserver.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  public void redirect(String token) {
    UserEntity userEntity = UserEntity.builder()
        .username(token.substring(0, 20))
        .email(token.substring(20, 40))
        .build();
    userRepository.save(userEntity);
    userRepository.flush();
  }
}
