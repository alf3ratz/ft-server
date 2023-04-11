package ru.alferatz.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.alferatz.travels.repository.UserRepository;
import ru.alferatz.travels.repository.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    final UserEntity customer = userRepository.getUserEntityByEmail(email);
    if (customer == null) {
      throw new UsernameNotFoundException(email);
    }

    return User.withUsername(customer.getEmail())
        .password("")
        .disabled(false)
        .authorities("USER").build();
  }
}
