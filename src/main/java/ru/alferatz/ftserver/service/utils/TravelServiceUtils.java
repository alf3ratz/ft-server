package ru.alferatz.ftserver.service.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.NotFoundException;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.repository.entity.UserEntity;

public class TravelServiceUtils {

  public void linkParticipantToChat(UserRepository userRepository, String participantEmail,
      Long chatId) {
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setChatId(chatId);
    userRepository.saveAndFlush(user);
  }

  public void unlinkParticipantFromChat(UserRepository userRepository, String participantEmail) {
    var user = userRepository.getUserEntityByEmail(participantEmail).orElse(null);
    if (user == null) {
      throw new NotFoundException("Пользователь не был найден в системе");
    }
    user.setChatId(null);
    userRepository.saveAndFlush(user);
  }

  public void linkParticipantToTravel(UserRepository userRepository, String participantEmail,
      Long travelId) {
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

  public void unlinkParticipantToTravel(UserRepository userRepository, String participantEmail,
      Long travelId) {
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

  public boolean isTravelChanged(TravelEntity travelEntity, TravelDto travelDto) {
    return !travelEntity.getCountOfParticipants().equals(travelDto.getCountOfParticipants()) ||
        !travelEntity.getPlaceFrom().equals(travelDto.getPlaceFrom()) ||
        !travelEntity.getPlaceTo().equals(travelDto.getPlaceTo()) ||
        !travelEntity.getComment().trim().toLowerCase(Locale.ROOT)
            .equals(travelDto.getComment().trim().toLowerCase(
                Locale.ROOT));
  }

  public List<UserDto> getUserDtoListFromUserEntityList(UserRepository userRepository,
      Long travelId) {
    List<UserDto> userDtoList = new ArrayList<>();
    List<UserEntity> usersInTravel = userRepository.getAllByTravelId(travelId)
        .orElse(Collections.emptyList());
    usersInTravel.forEach(i -> userDtoList.add(new UserDto(i.getUsername(), i.getEmail())));
    return userDtoList;
  }
}
