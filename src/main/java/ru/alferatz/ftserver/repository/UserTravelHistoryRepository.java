package ru.alferatz.ftserver.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.repository.entity.UserTravelHistoryEntity;

@Repository
public interface UserTravelHistoryRepository extends JpaRepository<UserTravelHistoryEntity, Long> {

  List<UserTravelHistoryEntity> findAllByTravelId(Long travelId);

  List<UserTravelHistoryEntity> findAllByUserId(Long userId);

}
