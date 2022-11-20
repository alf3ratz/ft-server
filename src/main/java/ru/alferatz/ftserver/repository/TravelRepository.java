package ru.alferatz.ftserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
}
