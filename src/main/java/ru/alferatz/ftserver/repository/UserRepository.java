package ru.alferatz.ftserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.repository.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity getUserEntityByEmail(String userEmail);

    @Query("update u from user_jn u " +
            "set u.travel_id = :travelId" +
            " where t.email = :userEmail ")
    void updateTravelAtUserEntityByEmail(String userEmail, Long travelId);


}
