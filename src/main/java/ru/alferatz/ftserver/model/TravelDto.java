package ru.alferatz.ftserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelDto {

  private Long id;

  private String authorEmail;

  private String createTime;

  private String startTime;

  private String placeFrom;

  private String placeTo;

  @JsonProperty("participants")
  private List<UserDto> participants;

  private Integer countOfParticipants;

  @Builder.Default
  private String comment = "";
  @Builder.Default
  private Long chatId = 0L;

  private CoordsObject placeFromCoords;

  private CoordsObject placeToCoords;
  private String travelStatus;
  private Integer price;
}
