package ru.alferatz.ftserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

import java.util.List;
import java.util.Optional;

public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    TravelEntity getTravelEntitiesByAuthorEqualsAndTravelStatusEquals(String author, String travelStatus);


}
