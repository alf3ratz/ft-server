package ru.alferatz.ftserver.service;

import io.swagger.v3.oas.annotations.servers.Server;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.model.request.AddUserRequest;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public UserDto addUserToSystem(AddUserRequest request) {
    UserEntity existUser = userRepository.getUserEntityByEmail(request.getUserEmail()).orElse(null);
    if (existUser != null) {
      return UserDto.builder()
          .email(existUser.getEmail())
          .username(existUser.getUsername())
          .build();
    }
    UserEntity userEntity = UserEntity.builder()
        .username(request.getUsername())
        .email(request.getUserEmail())
        .build();
    UserDto userDto = UserDto.builder()
        .username(request.getUsername())
        .email(request.getUserEmail())
        .build();
    try {
      userRepository.save(userEntity);
      return userDto;
    } catch (RuntimeException ex) {
      throw new InternalServerError(ex.getMessage());
    } finally {
      userRepository.flush();
    }
  }

  public Optional<UserEntity> findByEmail(String email) {
    return userRepository.getUserEntityByEmail(email);
  }

  public UserEntity create(UserEntity userEntity) {
    try {
      return userRepository.save(userEntity);
    } catch (RuntimeException ex) {
      throw new InternalServerError(String.format("blabla: %s", ex.getMessage()));
    } finally {
      userRepository.flush();
    }
  }
}
