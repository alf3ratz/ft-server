package ru.alferatz.ftserver.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alferatz.ftserver.repository.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> getUserEntityByEmail(String userEmail);

  @Query(value = "update u from user_jn u " +
      "set u.travel_id = :travelId" +
      " where t.email = :userEmail ", nativeQuery = true)
  void updateTravelAtUserEntityByEmail(String userEmail, Long travelId);

  Optional<List<UserEntity>> getAllByTravelId(Long travelid);

}
