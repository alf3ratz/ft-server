package ru.alferatz.ftserver.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.ConversionService;
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
@RequiredArgsConstructor
public class TravelService {

  private final ConversionService conversionService;
  private final TravelRepository travelRepository;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final Set<String> statusesExceptClosed = new HashSet<>(Arrays
      .asList(TravelStatus.CREATED.name(), TravelStatus.PROCESSING.name(),
          TravelStatus.IN_PROGRESS.name()));
  private final Set<String> processingStatuses = new HashSet<>(Arrays
      .asList(TravelStatus.CREATED.name(), TravelStatus.PROCESSING.name()));

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
    if (user.getTravelId() != null) {
      throw new InternalServerError("Пользователь уже находится в поездке");
    }
    // Создали чат, в котором потому будут общаться попутчики
    ChatRoom newChatRoom = ChatRoom.builder()
        .author(travelDto.getAuthorEmail())
        .build();
    try {
      chatRoomRepository.save(newChatRoom);
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось создать чат во время создания поездки");
    } finally {
      chatRoomRepository.flush();
    }
    // Присоединили создателя поездки к чату
    linkParticipantToChat(travelDto.getAuthorEmail(), newChatRoom.getId());

    // Создали объявление, которое сохранится в базе
    travelEntity = TravelEntity.builder()
        .author(travelDto.getAuthorEmail())
        .createTime(LocalDateTime.now())
        .startTime(LocalDateTime.parse(travelDto.getStartTime()))
        .placeFrom(travelDto.getPlaceFrom())
        .placeTo(travelDto.getPlaceTo())
        .countOfParticipants(travelDto.getCountOfParticipants())
        .travelStatus(TravelStatus.CREATED.name())
        .comment(travelDto.getComment())
        .chatId(newChatRoom.getId())
        .build();
    user = userRepository.getUserEntityByEmail(travelDto.getAuthorEmail()).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователя, создающего поездку, нет в базе");
    }
    try {
      travelRepository.save(travelEntity);
      user.setTravelId(travelEntity.getId());
      userRepository.save(user);
      return travelEntity;
    } catch (RuntimeException ex) {
      throw new InternalServerError("Не удалось добавить поездку в базу");
    } finally {
      travelRepository.flush();
      userRepository.flush();
    }
  }

  public Pair<Page<TravelEntity>, Map<String, List<UserDto>>> getAllOpenTravels(Pageable request) {
    Map<String, List<UserDto>> travelIdToUserListMap = new HashMap<>();
    var openTravels = travelRepository.getAllByTravelStatusIn(processingStatuses, request);
    openTravels.forEach(i -> {
      travelIdToUserListMap.put(i.getAuthor(), getUserDtoListFromUserEntityList(i.getId()));
    });
    //getUserDtoListFromUserEntityList(travelEntity.getId());
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

  public Pair<TravelEntity, List<UserDto>> addParticipant(ConnectToTravelRequest request) {
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
    linkParticipantToTravel(request.getEmail(), travelEntity.getId());
    // Присоединили попутчика к чату поездки
    linkParticipantToChat(request.getEmail(), travelEntity.getChatId());

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
    // Удалили попутчика из чата поездки
    unlinkParticipantFromChat(request.getEmail());

    List<UserDto> userDtoList = getUserDtoListFromUserEntityList(travelEntity.getId());
    travelEntity.setCountOfParticipants(travelEntity.getCountOfParticipants() - 1);
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
        .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthorEmail(), statusesExceptClosed)
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
    if (user.getTravelId() != null) {
      throw new InternalServerError("Пользователь уже находится в поездке");
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
    try {
      userRepository.save(user);
    } catch (RuntimeException e) {
      throw new InternalServerError("Не удалось выйти из поездки");
    } finally {
      userRepository.flush();
    }
  }

  private void linkParticipantToChat(String participantEmail, Long chatId) {
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setChatId(chatId);
    userRepository.saveAndFlush(user);
  }

  private void unlinkParticipantFromChat(String participantEmail) {
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setChatId(null);
    userRepository.saveAndFlush(user);
  }

  public Integer deleteTravel(Long travelId) {
    if (travelId <= 0) {
      throw new BadRequestException("Wrong parameter value");
    }
    var travelEntity = travelRepository.findById(travelId).orElse(null);
    if (travelEntity == null) {
      throw new NotFoundException("Поездки не существует");
    }
    var participants = userRepository.getAllByTravelId(travelId).orElseGet(Collections::emptyList);
    participants.forEach(i -> {
      unlinkParticipantFromChat(i.getEmail());
      unlinkParticipantToTravel(i.getEmail(), travelEntity.getId());
    });
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
