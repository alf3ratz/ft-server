package ru.alferatz.travels.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alferatz.travels.exceptions.AlreadyExistException;
import ru.alferatz.travels.exceptions.BadRequestException;
import ru.alferatz.travels.exceptions.InternalServerError;
import ru.alferatz.travels.exceptions.NotFoundException;
import ru.alferatz.travels.model.TravelDto;
import ru.alferatz.travels.repository.TravelRepository;
import ru.alferatz.travels.repository.UserRepository;
import ru.alferatz.travels.repository.entity.TravelEntity;
import ru.alferatz.travels.utils.enums.TravelStatus;

import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class TravelService {

  private final ConversionService conversionService;
  private final TravelRepository travelRepository;
  private final UserRepository userRepository;
  private final Set<String> statusesExceptClosed = new HashSet<>(Arrays
      .asList(TravelStatus.CREATED.name(), TravelStatus.PROCESSING.name(),
          TravelStatus.IN_PROGRESS.name()));
  private final Set<String> processingStatuses = new HashSet<>(Arrays
      .asList(TravelStatus.CREATED.name(), TravelStatus.PROCESSING.name()));

  public TravelEntity createTravel(TravelDto travelDto) {
    if (!checkTravelDto(travelDto)) {
      throw new BadRequestException("Wrong parameter value");
    }
    TravelEntity travelEntity = travelRepository
        .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthor(), statusesExceptClosed)
        .orElse(null);
    if (travelEntity != null) {
      throw new AlreadyExistException("У пользователя уже имеется активная поездка");
    }
//    var lastTravelEntity = travelRepository.getTravelEntityWithMaxId().orElse(null);
//    Long travelId = 0L;
//    if (lastTravelEntity == null) {
//      travelId = 1L;
//    } else {
//      travelId = lastTravelEntity.getId() + 1L;
//    }
    travelEntity = TravelEntity.builder()
        .author(travelDto.getAuthor())
        .placeFrom(travelDto.getPlaceFrom())
        .placeTo(travelDto.getPlaceTo())
        .countOfParticipants(travelDto.getCountOfParticipants())
        .travelStatus(TravelStatus.CREATED.name())
        .comment(travelDto.getComment())
        .build();
    try {
      travelRepository.save(travelEntity);
      return travelEntity;
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось добавить поездку в базу");
    } finally {
      travelRepository.flush();
    }
  }

  public Page<TravelEntity> getAllOpenTravels(Pageable request) {
    return travelRepository.getAllByTravelStatusIn(processingStatuses, request);
  }

  private boolean checkTravelDto(TravelDto travelDto) {
    try {
      return !travelDto.getPlaceFrom().isEmpty() && !Objects.isNull(travelDto.getPlaceFrom()) &&
          !travelDto.getPlaceTo().isEmpty() && !Objects.isNull(travelDto.getPlaceTo()) &&
          travelDto.getCountOfParticipants() >= 0 && travelDto.getCountOfParticipants() <= 4 &&
          !travelDto.getAuthor().isEmpty();
    } catch (RuntimeException ex) {
      throw new BadRequestException("Wrong parameter value");
    }
  }

  public TravelEntity addParticipant(TravelDto travelDto) {
    TravelEntity travelEntity = checkTravel(travelDto);
    if (isTravelChanged(travelEntity, travelDto)) {
      var usersAtTravelBeforeReduce = userRepository.getAllByTravelId(travelEntity.getId())
          .orElse(null);
      if (usersAtTravelBeforeReduce == null) {
        throw new NotFoundException("Не найдено попутчиков, присоединившихся к поездке");
      }
      var userEmailsBeforeReduce = usersAtTravelBeforeReduce.stream().map(i -> i.getEmail())
          .toList();
      var userEmailsFromDto = travelDto.getParticipants().stream().map(i -> i.getEmail())
          .toList();
      var reducedUser = userEmailsBeforeReduce.stream()
          .filter(email -> !userEmailsFromDto.contains(email)).toList().get(0);
      linkParticipantToTravel(reducedUser, travelEntity.getId());
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      travelEntity.setPlaceFrom(travelDto.getPlaceFrom());
      travelEntity.setPlaceTo(travelDto.getPlaceTo());
      try {
        travelRepository.save(travelEntity);
        return travelEntity;
      } catch (RuntimeException ex) {
        throw new InternalServerError("Не удалось обновить поездку в базе");
      } finally {
        travelRepository.flush();
      }
    }
    return travelEntity;
  }

  public TravelEntity reduceParticipant(TravelDto travelDto) {
    TravelEntity travelEntity = checkTravel(travelDto);
    if (isTravelChanged(travelEntity, travelDto)) {
      var usersAtTravelBeforeReduce = userRepository.getAllByTravelId(travelEntity.getId())
          .orElse(null);
      if (usersAtTravelBeforeReduce == null) {
        throw new NotFoundException("Не найдено попутчиков, присоединившихся к поездке");
      }
      var userEmailsBeforeReduce = usersAtTravelBeforeReduce.stream().map(i -> i.getEmail())
          .toList();
      var userEmailsFromDto = travelDto.getParticipants().stream().map(i -> i.getEmail())
          .toList();
      var reducedUser = userEmailsFromDto.stream()
          .filter(email -> !userEmailsBeforeReduce.contains(email)).toList().get(0);
      unlinkParticipantToTravel(reducedUser, travelEntity.getId());
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      travelEntity.setPlaceFrom(travelDto.getPlaceFrom());
      travelEntity.setPlaceTo(travelDto.getPlaceTo());
      try {
        travelRepository.save(travelEntity);
        return travelEntity;
      } catch (RuntimeException ex) {
        throw new InternalServerError("Не удалось обновить поездку в базе");
      } finally {
        travelRepository.flush();
      }
    }
    return travelEntity;
  }

  public TravelEntity updateTravel(TravelDto travelDto) {
    TravelEntity travelEntity = checkTravel(travelDto);
    //var travelEntityFromDto = conversionService.convert(travelDto, TravelEntity.class);
    if (isTravelChanged(travelEntity, travelDto)) {
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      // TODO: подумать, как обновлять участников без полного цикла for (может быть лишняя работа)
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      travelEntity.setPlaceFrom(travelDto.getPlaceFrom());
      travelEntity.setPlaceTo(travelDto.getPlaceTo());
      travelEntity.setComment(travelDto.getComment());
      try {
        travelRepository.save(travelEntity);
        return travelEntity;
      } catch (RuntimeException ex) {
        throw new InternalServerError("Не удалось обновить поездку в базе");
      } finally {
        travelRepository.flush();
      }
    }
    return travelEntity;
  }

  /**
   * Проверяем, правильный ли объект пришел и есть ли поездка в базе
   *
   * @param travelDto - пришедший объект с информацией о поездке
   * @return - поездка из базы
   */
  private TravelEntity checkTravel(TravelDto travelDto) {
    if (!checkTravelDto(travelDto)) {
      throw new BadRequestException("Wrong parameter value");
    }
    TravelEntity travelEntity = travelRepository
        .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthor(), statusesExceptClosed)
        .orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не найдена");
    }
    return travelEntity;
  }

  private boolean isTravelChanged(TravelEntity travelEntity, TravelDto travelDto) {
//    var participants = travelDto.getParticipants();
//    for (UserDto participant : participants) {
//      var userEntity = userRepository.getUserEntityByEmail(participant.getEmail());
//      // Если у участника в пришедшем объекте нет связи с поездкой, то изменения точно есть
//      if (!userEntity.getTravelId().equals(travelEntity.getId())) {
//        return true;
//      }
//    }
    // Проверяем на изменения в количестве участников, месте посадки и высадки
    return !travelEntity.getCountOfParticipants().equals(travelDto.getCountOfParticipants()) ||
        !travelEntity.getPlaceFrom().equals(travelDto.getPlaceFrom()) ||
        !travelEntity.getPlaceTo().equals(travelDto.getPlaceTo()) ||
        !travelEntity.getComment().trim().toLowerCase(Locale.ROOT)
            .equals(travelDto.getComment().trim().toLowerCase(
                Locale.ROOT));
  }

  private void linkParticipantToTravel(String participantEmail, Long travelId) {
    var user = userRepository.getUserEntityByEmail(participantEmail);
    user.setTravelId(travelId);
    userRepository.saveAndFlush(user);
  }

  private void unlinkParticipantToTravel(String participantEmail, Long travelId) {
    var user = userRepository.getUserEntityByEmail(participantEmail);
    user.setTravelId(null);
    userRepository.saveAndFlush(user);
  }

  public Integer deleteTravel(TravelDto travelDto) {
    if (!checkTravelDto(travelDto)) {
      throw new BadRequestException("Wrong parameter value");
    }
    var deletedTravelCount = travelRepository.deleteTravelEntityByAuthor(travelDto.getAuthor());
    if (deletedTravelCount == 0) {
      throw new NotFoundException("Can not delete travel");
    }
    travelRepository.flush();
    return deletedTravelCount;
  }


}
