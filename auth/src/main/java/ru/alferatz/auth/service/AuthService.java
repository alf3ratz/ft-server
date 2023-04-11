package ru.alferatz.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alferatz.auth.error.UserAlreadyExistException;
import ru.alferatz.auth.model.VerificationToken;
import ru.alferatz.auth.repository.VerificationTokenRepository;
import ru.alferatz.travels.model.UserDto;
import ru.alferatz.travels.repository.UserRepository;
import ru.alferatz.travels.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository repository;

  private final VerificationTokenRepository tokenRepository;

  public UserEntity registerNewUserAccount(UserDto userDto)
      throws UserAlreadyExistException {

    if (emailExist(userDto.getEmail())) {
      throw new UserAlreadyExistException(
          "There is an account with that email adress: "
              + userDto.getEmail());
    }

    UserEntity user = new UserEntity();
    user.setUsername(userDto.getUsername());
    user.setEmail(userDto.getEmail());
    //user.setRole(new Role(Integer.valueOf(1), user));
    return repository.save(user);
  }

  private boolean emailExist(String email) {
    return repository.getUserEntityByEmail(email) != null;
  }

  public UserEntity getUser(String verificationToken) {
    UserEntity user = tokenRepository.findByToken(verificationToken).getUser();
    return user;
  }

  public VerificationToken getVerificationToken(String VerificationToken) {
    return tokenRepository.findByToken(VerificationToken);
  }

  public void saveRegisteredUser(UserEntity user) {
    repository.save(user);
  }

  public void createVerificationToken(UserEntity user, String token) {
    VerificationToken myToken = new VerificationToken(token, user);
    tokenRepository.save(myToken);
  }
}
