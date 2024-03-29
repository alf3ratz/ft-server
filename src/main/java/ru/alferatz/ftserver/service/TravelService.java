package ru.alferatz.ftserver.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alferatz.ftserver.chat.entity.ChatRoom;
import ru.alferatz.ftserver.chat.repository.ChatRoomRepository;
import ru.alferatz.ftserver.exceptions.AlreadyExistException;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.NotFoundException;
import ru.alferatz.ftserver.model.TravelDtoFactory;
import ru.alferatz.ftserver.model.request.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.UserTravelHistoryRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.repository.entity.UserTravelHistoryEntity;
import ru.alferatz.ftserver.service.utils.TravelServiceUtils;
import ru.alferatz.ftserver.utils.enums.TravelStatus;

import java.util.*;


@Service
@RequiredArgsConstructor
public class TravelService {

  private final TravelServiceUtils travelServiceUtils;
  private final TravelRepository travelRepository;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserTravelHistoryRepository userTravelHistoryRepository;
  private final TravelDtoFactory travelDtoFactory;

  private final Set<String> statusesExceptClosed = new HashSet<>(Arrays
      .asList(TravelStatus.CREATED.name(), TravelStatus.IN_PROGRESS.name()));
  private final Set<String> processingStatuses = new HashSet<>(
      Collections.singletonList(TravelStatus.CREATED.name()));
  private final Set<String> closedStatus = new HashSet<>(
      Collections.singletonList(TravelStatus.CLOSED.name()));

  /**
   * Создание объявления
   *
   * @param travelDto - объект с информацией об объявлении
   */
  public TravelEntity createTravel(TravelDto travelDto) {
    if (!checkTravelDto(travelDto)) {
      throw new BadRequestException("Wrong parameter value");
    }
    TravelEntity travelEntity = travelRepository
        .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthorEmail(), statusesExceptClosed)
        .orElse(null);
    if (travelEntity != null) {
      throw new AlreadyExistException("У пользователя уже имеется активная поездка");
    }
    UserEntity user = userRepository.getUserEntityByEmail(travelDto.getAuthorEmail()).orElse(null);
    if (user == null) {
      throw new InternalServerError("Пользователь отсутствует в системе");
    }
    if (user.getTravelId() != null) {
      throw new InternalServerError("Пользователь уже находится в поездке");
    }
    // Создали чат, в котором потому будут общаться попутчики
    ChatRoom newChatRoom = ChatRoom.builder()
        .author(travelDto.getAuthorEmail())
        .build();
    try {
      newChatRoom = chatRoomRepository.save(newChatRoom);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось создать чат во время создания поездки");
    } finally {
      chatRoomRepository.flush();
    }

    // Присоединили создателя поездки к чату
    travelServiceUtils
        .linkParticipantToChat(userRepository, travelDto.getAuthorEmail(), newChatRoom.getId());

    // Создали объявление, которое сохранится в базе
    travelEntity = TravelEntity.builder()
        .author(travelDto.getAuthorEmail())
        .createTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .startTime(LocalDateTime.parse(travelDto.getStartTime()))
        .placeFrom(travelDto.getPlaceFrom())
        .placeTo(travelDto.getPlaceTo())
        .countOfParticipants(travelDto.getCountOfParticipants())
        .travelStatus(TravelStatus.CREATED.name())
        .comment(travelDto.getComment())
        .placeFromCoords(travelDto.getPlaceFromCoords())
        .placeToCoords(travelDto.getPlaceToCoords())
        .chatId(newChatRoom.getId())
        .price(travelDto.getPrice())
        .build();

    try {
      travelEntity = travelRepository.save(travelEntity);
      travelServiceUtils
          .linkParticipantToTravel(userRepository, travelEntity.getAuthor(), travelEntity.getId());
      return travelEntity;
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось добавить поездку в базу");
    } finally {
      travelRepository.flush();
    }
//    try {
//      var travel = travelRepository.getTravelEntitiesByAuthor(user.getEmail()).orElse(null);
//      user.setTravelId(travel.getId());
//      userRepository.save(user);
//      return travelEntity;
//    } catch (RuntimeException ex) {
//      throw new InternalServerError("Не удалось добавить поездку в базу");
//    } finally {
//      userRepository.flush();
//    }
  }

  public Pair<Page<TravelEntity>, Map<String, List<UserDto>>> getAllOpenTravels(Pageable request) {
    Map<String, List<UserDto>> travelIdToUserListMap = new HashMap<>();
    var openTravels = travelRepository.getAllByTravelStatusIn(processingStatuses, request);
    openTravels.forEach(i -> {
      var participants = travelServiceUtils
          .getUserDtoListFromUserEntityList(userRepository, i.getId(), i.getAuthor());
      participants.removeIf(j -> j.getEmail().equals(i.getAuthor()));
      travelIdToUserListMap.put(i.getAuthor(), participants);
    });
    return Pair.of(openTravels, travelIdToUserListMap);
  }

  private boolean checkTravelDto(TravelDto travelDto) {
    try {
      return !travelDto.getPlaceFrom().isEmpty() && !Objects.isNull(travelDto.getPlaceFrom()) &&
          !travelDto.getPlaceTo().isEmpty() && !Objects.isNull(travelDto.getPlaceTo()) &&
          travelDto.getCountOfParticipants() >= 0 && travelDto.getCountOfParticipants() <= 4 &&
          !travelDto.getAuthorEmail().isEmpty() || LocalDateTime.parse(travelDto.getStartTime())
          .isBefore(LocalDateTime.now());
    } catch (RuntimeException ex) {
      throw new BadRequestException("Wrong parameter value");
    }
  }

  public TravelDto addParticipant(ConnectToTravelRequest request) {
    if (request.getTravelId() == null || request.getEmail().isEmpty()) {
      throw new BadRequestException("Неверный запрос");
    }
    TravelEntity travelEntity = travelRepository.findById(request.getTravelId()).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не была найдена");
    }
    UserEntity user = userRepository.getUserEntityByEmail(request.getEmail()).orElse(null);
    if (user.getTravelId() != null) {
      throw new InternalServerError("Пользователь уже находится в поездке");
    }
    travelServiceUtils
        .linkParticipantToTravel(userRepository, request.getEmail(), travelEntity.getId());
    // Присоединили попутчика к чату поездки
    travelServiceUtils
        .linkParticipantToChat(userRepository, request.getEmail(), travelEntity.getChatId());

    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(),
            travelEntity.getAuthor());
    userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
    travelEntity.setCountOfParticipants(travelEntity.getCountOfParticipants() + 1);
    try {
      travelRepository.save(travelEntity);
      return travelServiceUtils.buildTravelDto(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось обновить поездку в базе");
    } finally {
      travelRepository.flush();
    }
  }

  public TravelDto reduceParticipant(ConnectToTravelRequest request) {
    if (request.getTravelId() == null || request.getEmail().isEmpty()) {
      throw new BadRequestException("Неверный запрос");
    }
    TravelEntity travelEntity = travelRepository.findById(request.getTravelId()).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не была найдена");
    }
    travelServiceUtils
        .unlinkParticipantToTravel(userRepository, request.getEmail(), travelEntity.getId());
    // Удалили попутчика из чата поездки
    travelServiceUtils.unlinkParticipantFromChat(userRepository, request.getEmail());

    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(),
            travelEntity.getAuthor());
    userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
    travelEntity.setCountOfParticipants(travelEntity.getCountOfParticipants() - 1);
    try {
      travelRepository.save(travelEntity);
      return travelServiceUtils.buildTravelDto(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось обновить поездку в базе");
    } finally {
      travelRepository.flush();
    }
  }

  public TravelDto updateTravel(TravelDto travelDto) {
    TravelEntity travelEntity = checkTravel(travelDto);
    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(),
            travelEntity.getAuthor());
    userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
    travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
    travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
    travelEntity.setPlaceFrom(travelDto.getPlaceFrom());
    travelEntity.setPlaceTo(travelDto.getPlaceTo());
    travelEntity.setComment(travelDto.getComment());
    travelEntity.setStartTime(LocalDateTime.parse(travelDto.getStartTime()));
    travelEntity.setPlaceFromCoords(travelDto.getPlaceFromCoords());
    travelEntity.setPlaceToCoords(travelDto.getPlaceToCoords());
    travelEntity.setPrice(travelDto.getPrice());
    if (travelDto.getPlaceFromCoords() != null && travelDto.getPlaceToCoords() != null) {
      travelEntity.setPlaceFromCoords(travelDto.getPlaceFromCoords());
      travelEntity.setPlaceToCoords(travelDto.getPlaceToCoords());
    }
    try {
      travelRepository.save(travelEntity);
      return travelDtoFactory.makeTravelDto(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось обновить поездку в базе");
    } finally {
      travelRepository.flush();
    }
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
        .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthorEmail(), statusesExceptClosed)
        .orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездка не найдена или уже закрыта");
    }
    return travelEntity;
  }

  public Long deleteTravel(Long travelId) {
    if (travelId <= 0) {
      throw new BadRequestException("Wrong parameter value");
    }
    var travelEntity = travelRepository.findById(travelId).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездки не существует");
    }
    var participants = userRepository.getAllByTravelId(travelId).orElseGet(Collections::emptyList);
    participants.forEach(i -> {
      userTravelHistoryRepository.save(UserTravelHistoryEntity.builder()
          .userId(i.getId())
          .travelId(travelEntity.getId())
          .chatId(travelEntity.getChatId())
          .build());
      travelServiceUtils.unlinkParticipantFromChat(userRepository, i.getEmail());
      travelServiceUtils
          .unlinkParticipantToTravel(userRepository, i.getEmail(), travelEntity.getId());
    });
    try {
      travelEntity.setTravelStatus(TravelStatus.CLOSED.name());
      travelRepository.save(travelEntity);
      return travelId;
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось удалить поездку");
    } finally {
      travelRepository.flush();
      userTravelHistoryRepository.flush();
    }
  }

  public TravelDto startTravel(Long travelId) {
    TravelEntity startingTravel = travelRepository.findById(travelId).orElse(new TravelEntity());
    startingTravel.setTravelStatus(TravelStatus.IN_PROGRESS.name());
    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelId, startingTravel.getAuthor());
    try {
      travelRepository.save(startingTravel);
      return travelServiceUtils.buildTravelDto(startingTravel, userDtoList);
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось изменить статус поездки");
    } finally {
      travelRepository.flush();
    }
  }

  public TravelDto stopTravel(Long travelId) {
    TravelEntity closingTravel = travelRepository.findById(travelId).orElse(new TravelEntity());
    closingTravel.setTravelStatus(TravelStatus.CLOSED.name());
    List<UserDto> userDtoList = new ArrayList<>();
    List<UserEntity> usersInTravel = userRepository.getAllByTravelId(travelId)
        .orElse(Collections.emptyList());
    usersInTravel.forEach(i -> userDtoList.add(new UserDto(i.getUsername(), i.getEmail())));
    userDtoList.removeIf(i -> i.getEmail().equals(closingTravel.getAuthor()));
    // Удаляем связь каждого пользователя с поездкой и чатом
    usersInTravel.forEach(i -> {
      userTravelHistoryRepository.save(UserTravelHistoryEntity.builder()
          .userId(i.getId())
          .travelId(closingTravel.getId())
          .chatId(closingTravel.getChatId())
          .build());
      travelServiceUtils.unlinkParticipantFromChat(userRepository, i.getEmail());
      travelServiceUtils
          .unlinkParticipantToTravel(userRepository, i.getEmail(), closingTravel.getId());
    });

    try {
      travelRepository.save(closingTravel);
      return travelServiceUtils.buildTravelDto(closingTravel, userDtoList);
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось изменить статус поездки");
    } finally {
      travelRepository.flush();
      userTravelHistoryRepository.flush();
    }
  }


  public TravelDto getTravelById(Long travelId) {
    if (travelId <= 0) {
      throw new BadRequestException("Неверный travelId");
    }
    TravelEntity travelEntity = travelRepository.findById(travelId).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Запрашиваемой поездки не существует");
    }
    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(),
            travelEntity.getAuthor());
    userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
    return travelDtoFactory.makeTravelDto(travelEntity, userDtoList);
  }

  public TravelDto getTravelByUserEmail(String authorEmail) {
    authorEmail = authorEmail.trim();
    UserEntity user = userRepository.getUserEntityByEmail(authorEmail).orElse(null);
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
    List<UserDto> userDtoList = travelServiceUtils
        .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(), authorEmail);
    userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
    return travelDtoFactory.makeTravelDto(travelEntity, userDtoList);
  }

  /**
   * Получение всех закрытых поездок, метод для истории поездок
   */
  public Pair<List<TravelEntity>, Map<Long, List<UserDto>>> getTravelHistoryByEmail(
      String userEmail) {
    Map<Long, List<UserDto>> travelIdToUserListMap = new HashMap<>();
    List<TravelEntity> closedTravelEntities = new ArrayList<>();
    // Получаем все поездки, где автором является пользователь, который указан в запросе
//    var closedTravelsCreatedByUser = travelRepository
//        .getAllByTravelStatusInAndAuthorEquals(closedStatus, userEmail, request);
//    closedTravelsCreatedByUser.forEach(i -> {
//      travelIdToUserListMap.put(i.getAuthor(),
//          travelServiceUtils.getUserDtoListFromUserEntityList(userRepository, i.getId()));
//    });
    UserEntity user = userRepository.getUserEntityByEmail(userEmail).orElse(new UserEntity());
    var listOfTravelIds = travelServiceUtils
        .getHistoryTravelIdsByUserId(userTravelHistoryRepository, user.getId());
    listOfTravelIds.forEach(travelId -> {
      var travelEntity = travelRepository.findById(travelId).orElse(new TravelEntity());
      var userDtoList = travelServiceUtils
          .getUsersFromTravelInHistory(userRepository, userTravelHistoryRepository, travelId);
      // Удаляем пользователя из списка попутчиков, если он автор поездки
      userDtoList.removeIf(i -> i.getEmail().equals(travelEntity.getAuthor()));
      closedTravelEntities.add(travelEntity);
      travelIdToUserListMap.put(travelId, userDtoList);
    });
    //

    return Pair.of(closedTravelEntities, travelIdToUserListMap);
  }

  public TravelDto setLeadershipToParticipant(Long travelId, String participantEmail) {
    TravelEntity travelEntity = travelRepository.findById(travelId).orElse(new TravelEntity());
    String authorEmail = travelEntity.getAuthor();
    if (authorEmail.equals(participantEmail.trim())) {
      throw new InternalServerError("Автор не может передать лидерство самому себе");
    }
    travelEntity.setAuthor(participantEmail);
    try {
      travelRepository.save(travelEntity);
      List<UserDto> userDtoList = travelServiceUtils
          .getUserDtoListFromUserEntityList(userRepository, travelEntity.getId(), participantEmail);
      return travelServiceUtils.buildTravelDto(travelEntity, userDtoList);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось передать лидерство поездкой");
    } finally {
      travelRepository.flush();
    }
  }

  @Transactional
  public void closeTravelsIfNeeded() {
    var travelEntities = travelRepository.getAllTravelsByCondition();
    var localTime = LocalDateTime.now();
    travelEntities = travelEntities.stream().filter(i -> {
      long hours = ChronoUnit.HOURS.between(i.getStartTime(), localTime);
      return hours > 3;
    }).collect(Collectors.toList());
    try {
      travelEntities.forEach(i -> {
        var participants = userRepository.getAllByTravelId(i.getId())
            .orElseGet(Collections::emptyList);
        participants.forEach(j -> {
          userTravelHistoryRepository.save(UserTravelHistoryEntity.builder()
              .userId(j.getId())
              .travelId(i.getId())
              .chatId(i.getChatId())
              .build());
          travelServiceUtils.unlinkParticipantFromChat(userRepository, j.getEmail());
          travelServiceUtils
              .unlinkParticipantToTravel(userRepository, j.getEmail(), i.getId());
        });
        travelRepository.setStatusToTravel(i.getId(), TravelStatus.CLOSED.name());
      });
    } catch (RuntimeException ex) {
      throw new InternalServerError(
          String.format("Не удалось закрыть незакрыте объявления: %s", ex.getMessage()));
    }
  }
}
