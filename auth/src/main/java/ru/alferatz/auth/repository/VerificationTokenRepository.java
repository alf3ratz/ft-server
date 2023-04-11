package ru.alferatz.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alferatz.auth.model.VerificationToken;
import ru.alferatz.travels.repository.entity.UserEntity;

@Repository
public interface VerificationTokenRepository
    extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  VerificationToken findByUser(UserEntity user);
}