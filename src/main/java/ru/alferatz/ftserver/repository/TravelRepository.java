package ru.alferatz.ftserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

import java.util.List;
import java.util.Optional;

public interface TravelRepository extends JpaRepository<TravelEntity, Long> {
    @Query("select t from travel_jn t where t.author = :author and  t.travel_status in ('CREATED','PROCESSING','IN_PROGRESS')")
    TravelEntity getCurrentTravelByAuthor(String author);
}
