package ru.alferatz.ftserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.utils.enums.TravelStatusEnum;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    //@Query(value = "select t from travel_jn t where t.author = :author and  t.travel_status in ('CREATED','PROCESSING','IN_PROGRESS')", nativeQuery = true)
    //TravelEntity getCurrentTravelByAuthor(String author);
    TravelEntity getTravelEntityByAuthorAndTravelStatusIn(String author, Collection<String> travelStatus);

    Integer deleteTravelEntityByAuthor(String author);
}
