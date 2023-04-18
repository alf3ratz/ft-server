package ru.alferatz.ftserver.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alferatz.ftserver.exceptions.AlreadyExistException;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.NotFoundException;
import ru.alferatz.ftserver.model.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.utils.enums.TravelStatus;

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
    travelEntity = TravelEntity.builder()
        .author(travelDto.getAuthor())
        .createTime(LocalDateTime.now())
        .startTime(travelDto.getStartTime())
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
          !travelDto.getAuthor().isEmpty() || travelDto.getStartTime()
          .isBefore(LocalDateTime.now());
    } catch (RuntimeException ex) {
      throw new BadRequestException("Wrong parameter value");
    }
  }

  public Pair<TravelEntity, List<UserDto>> addParticipant(ConnectToTravelRequest request) {
    if (request.getTravelId() == null || request.getEmail().isEmpty()) {
      throw new BadRequestException("Неверный запрос");
    }
    TravelEntity travelEntity = travelRepository.findById(request.getTravelId()).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не была найдена");
    }
    linkParticipantToTravel(request.getEmail(), travelEntity.getId());
    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    travelEntity.setCountOfParticipants(travelEntity.getCountOfParticipants() + 1);
    try {
      travelRepository.save(travelEntity);
      return Pair.of(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось обновить поездку в базе");
    } finally {
      travelRepository.flush();
    }
  }

  public Pair<TravelEntity, List<UserDto>> reduceParticipant(ConnectToTravelRequest request) {
    if (request.getTravelId() == null || request.getEmail().isEmpty()) {
      throw new BadRequestException("Неверный запрос");
    }
    TravelEntity travelEntity = travelRepository.findById(request.getTravelId()).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не была найдена");
    }
    unlinkParticipantToTravel(request.getEmail(), travelEntity.getId());
    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    travelEntity.setCountOfParticipants(travelEntity.getCountOfParticipants() + 1);
    try {
      travelRepository.save(travelEntity);
      return Pair.of(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось обновить поездку в базе");
    } finally {
      travelRepository.flush();
    }
  }

  public TravelEntity addParticipantOld(TravelDto travelDto) {
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

  public TravelEntity reduceParticipantOld(TravelDto travelDto) {
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

  private List<UserDto> getUserDtoListFromUserEntityList(Long travelId) {
    List<UserDto> userDtoList = new ArrayList<>();
    List<UserEntity> usersInTravel = userRepository.getAllByTravelId(travelId)
        .orElse(Collections.emptyList());
    usersInTravel.forEach(i -> userDtoList.add(new UserDto(i.getUsername(), i.getEmail())));
    return userDtoList;
  }

  public Pair<TravelEntity, List<UserDto>> updateTravel(TravelDto travelDto) {
    TravelEntity travelEntity = checkTravel(travelDto);
    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    if (isTravelChanged(travelEntity, travelDto)) {
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      // TODO: подумать, как обновлять участников без полного цикла for (может быть лишняя работа)
      travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
      travelEntity.setPlaceFrom(travelDto.getPlaceFrom());
      travelEntity.setPlaceTo(travelDto.getPlaceTo());
      travelEntity.setComment(travelDto.getComment());
      try {
        travelRepository.save(travelEntity);
        return Pair.of(travelEntity, userDtoList);
      } catch (RuntimeException ex) {
        throw new InternalServerError("Не удалось обновить поездку в базе");
      } finally {
        travelRepository.flush();
      }
    }
    return Pair.of(travelEntity, userDtoList);
  }

  public Pair<TravelEntity, List<UserDto>> getTravelById(Long travelId) {
    if (travelId <= 0) {
      throw new BadRequestException("Неверный travelId");
    }
    TravelEntity travelEntity = travelRepository.findById(travelId).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Запрашиваемой поездки не существует");
    }
    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    return Pair.of(travelEntity, userDtoList);
  }

  public Pair<TravelEntity, List<UserDto>> getTravelByUserEmail(String email) {
    email = email.trim();
    UserEntity user = userRepository.getUserEntityByEmail(email).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    if (user.getTravelId() == null) {
      throw new NotFoundException("Пользователь не находится в поездке");
    }
    TravelEntity travelEntity = travelRepository.findById(user.getTravelId()).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не найдена в системе");
    }
    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    return Pair.of(travelEntity, userDtoList);
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
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setTravelId(travelId);
    userRepository.saveAndFlush(user);
  }

  private void unlinkParticipantToTravel(String participantEmail, Long travelId) {
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setTravelId(null);
    userRepository.saveAndFlush(user);
  }

  public Integer deleteTravel(Long travelId) {
    if (travelId <= 0) {
      throw new BadRequestException("Wrong parameter value");
    }

    try {
      var deletedTravelCount = travelRepository.deleteTravelEntityById(travelId);
      if (deletedTravelCount == 0) {
        throw new NotFoundException("Can not delete travel");
      }
      return deletedTravelCount;
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось удалить поездку");
    } finally {
      travelRepository.flush();
    }
  }


}
