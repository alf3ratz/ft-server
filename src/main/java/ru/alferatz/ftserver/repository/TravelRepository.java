package ru.alferatz.ftserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TravelRepository extends JpaRepository<TravelEntity, Long> {

  //@Query(value = "select t from travel_jn t where t.author = :author and  t.travel_status in ('CREATED','PROCESSING','IN_PROGRESS')", nativeQuery = true)
  //TravelEntity getCurrentTravelByAuthor(String author);
  Optional<TravelEntity> getTravelEntityByAuthorAndTravelStatusIn(String author,
      Collection<String> travelStatuses);

  Page<TravelEntity> getAllByTravelStatusIn(Collection<String> travelStatuses, Pageable request);

  Optional<TravelEntity> getTravelEntitiesByAuthor(String author);

  Integer deleteTravelEntityByAuthor(String author);

//  @Query(value = "SELECT new TravelEntity (MAX (t.id),t.author,t.placeFrom,t.placeTo,t.countOfParticipants,t.travelStatus,t.comment ) FROM TravelEntity t GROUP BY t")
//  Optional<TravelEntity> getTravelEntityWithMaxId();

  Integer deleteTravelEntityById(Long id);
}
