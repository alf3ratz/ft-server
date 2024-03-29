package ru.alferatz.ftserver.model;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.alferatz.ftserver.repository.entity.TravelEntity;

@Component
public class TravelDtoFactory {

  public TravelDto makeTravelDto(TravelEntity travelEntity, List<UserDto> participants) {
    return TravelDto.builder()
        .id(travelEntity.getId())
        .authorEmail(travelEntity.getAuthor())
        .createTime(travelEntity.getCreateTime().toString())
        .startTime(travelEntity.getStartTime().toString())
        .countOfParticipants(travelEntity.getCountOfParticipants())
        .placeFrom(travelEntity.getPlaceFrom())
        .placeTo(travelEntity.getPlaceTo())
        .chatId(travelEntity.getChatId())
        .participants(participants)
        .comment(travelEntity.getComment())
        .travelStatus(travelEntity.getTravelStatus())
        .placeFromCoords(travelEntity.getPlaceFromCoords())
        .placeToCoords(travelEntity.getPlaceToCoords())
        .price(travelEntity.getPrice())
        .build();
  }
}
